package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class PythonExecStrategy implements ExecStrategy, ExcecRequiresFiles, Settings {
    private ScriptDTO scriptDTO;

    //Constructor for net.dmi3.scriptexec.infrastructure.Lang
    PythonExecStrategy() {
    }

    public PythonExecStrategy(ScriptDTO scriptDTO) {
        this.scriptDTO = scriptDTO;
    }

    @Override
    public ExecStrategy getForScript(ScriptDTO scriptDTO) {
        return new PythonExecStrategy(scriptDTO);
    }

    @Override
    public String getFile() {
        return scriptDTO.script;
    }

    @Override
    public String getFileName() {
        return COMPILATION_DIR + "/" + scriptDTO.id + ".py";
    }

    @Override
    public List<String> getFilesForCleanup() {
        return Arrays.asList(getFileName(), COMPILATION_DIR + "/" + scriptDTO.id + ".pyc");
    }

    @Override
    public List<String> getCmd() {
        return Arrays.asList("python "+getFileName());
    }
}
