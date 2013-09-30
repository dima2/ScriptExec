package net.dmi3.scriptexec.service.impl;

import com.google.inject.Inject;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.service.Excecutor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class SimpleOsExcecutor implements Excecutor {
    private static Logger LOG = Logger.getLogger(SimpleOsExcecutor.class.getName());

    @Inject
    private MemoryBasedKeeper keeper;

    @Override
    public Long submit(ScriptDTO script) {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec(script.script);
            process.waitFor();

            script.result = IOUtils.toString(process.getInputStream());

        } catch (IOException | InterruptedException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }

        return keeper.put(script);
    }

}
