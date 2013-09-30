package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.entity.ScriptDTO;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class ShellExecStrategy implements ExecStrategy {
    private ScriptDTO scriptDTO;

    //Constructor for net.dmi3.scriptexec.infrastructure.Lang
    ShellExecStrategy() {
    }

    public ShellExecStrategy(ScriptDTO scriptDTO) {
        this.scriptDTO = scriptDTO;
    }

    @Override
    public ExecStrategy getForScript(ScriptDTO scriptDTO) {
        return new ShellExecStrategy(scriptDTO);
    }

    @Override
    public List<String> getCmd() {
        return Arrays.asList(scriptDTO.script.split("\\s*\\&\\&\\s*"));
    }
}
