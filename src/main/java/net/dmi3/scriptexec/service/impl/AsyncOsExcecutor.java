package net.dmi3.scriptexec.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.entity.Status;
import net.dmi3.scriptexec.infrastructure.ExcecRequiresFiles;
import net.dmi3.scriptexec.infrastructure.ExecStrategy;
import net.dmi3.scriptexec.infrastructure.Lang;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

@Singleton
public class AsyncOsExcecutor implements net.dmi3.scriptexec.service.Excecutor, Settings {
    private static Logger LOG = Logger.getLogger(AsyncOsExcecutor.class.getName());

    @Inject
    private MemoryBasedKeeper keeper;

    protected ExecutorService exec = Executors.newFixedThreadPool(4);

    protected Runtime runtime = Runtime.getRuntime();

    protected void writeFile(ExcecRequiresFiles execStrategy) throws IOException {
        File file = new File(execStrategy.getFileName());
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        IOUtils.write(execStrategy.getFile(), new FileOutputStream(file));
    }

    protected void removeFiles(ExcecRequiresFiles execStrategy) {
        for (String path : execStrategy.getFilesForCleanup()) {
            File file = new File(path);
            file.delete();
        }
    }

    @Override
    public Long submit(ScriptDTO script) {
        Long id = keeper.put(script);
        FutureTask<ScriptDTO> executionTask = new FutureTask<>(new Excecutor(script.copy(), runtime));
        exec.execute(executionTask);
        exec.execute(new Updater(id, keeper, executionTask));

        return id;
    }

    class Excecutor implements Callable<ScriptDTO> {
        private ScriptDTO scriptCopy;
        private Runtime runtime;

        public Excecutor(ScriptDTO scriptCopy, Runtime runtime) {
            this.scriptCopy = scriptCopy;
            this.runtime = runtime;
        }

        @Override
        public ScriptDTO call() throws Exception {
            try {
                ExecStrategy execStrategy = Lang.getExecStrategyForScript(scriptCopy);


                if (execStrategy instanceof ExcecRequiresFiles) {
                    writeFile((ExcecRequiresFiles) execStrategy);
                }

                List<String> commands = execStrategy.getCmd();

                scriptCopy.result = "";
                for (String command : commands) {
                    Process process = runtime.exec(command);
                    process.waitFor();

                    if (process.exitValue() == 0) {
                        scriptCopy.result = scriptCopy.result + IOUtils.toString(process.getInputStream());
                        scriptCopy.status = Status.EXCECUTED;
                    } else {
                        scriptCopy.result = IOUtils.toString(process.getErrorStream());
                        scriptCopy.status = Status.SCRIPT_ERROR;
                        break;
                    }
                }

                if (execStrategy instanceof ExcecRequiresFiles) {
                    removeFiles((ExcecRequiresFiles) execStrategy);
                }

            } catch (IOException e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                LOG.info("Script " + scriptCopy.id + " terminated");
            }

            return scriptCopy;
        }
    }

    protected class Updater implements Runnable {

        private Long id;
        private MemoryBasedKeeper keeper;
        private FutureTask<ScriptDTO> excecutorTask;

        public Updater(Long id, MemoryBasedKeeper keeper, FutureTask<ScriptDTO> excecutorTask) {
            this.id = id;
            this.keeper = keeper;
            this.excecutorTask = excecutorTask;
        }

        @Override
        public void run() {
            ScriptDTO scriptDTO;
            try {
                scriptDTO = excecutorTask.get(TIMEOUT_SEC, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                scriptDTO = getScriptCopy();
                scriptDTO.status = Status.SCRIPT_ERROR;

                LOG.error(ExceptionUtils.getStackTrace(e));
            } catch (TimeoutException e) {
                scriptDTO = getScriptCopy();
                scriptDTO.status = Status.TIMEOUT;
                excecutorTask.cancel(true);

                LOG.info("Script " + id + " timeout");
            }
            keeper.replace(scriptDTO);
        }

        private ScriptDTO getScriptCopy() {
            ScriptDTO scriptDTO = keeper.get(id);
            if (scriptDTO != null) {
                return scriptDTO.copy();
            } else {
                scriptDTO = new ScriptDTO();
                scriptDTO.id = id;
                return scriptDTO;
            }
        }
    }
}
