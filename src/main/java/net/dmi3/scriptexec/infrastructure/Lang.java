package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.entity.ScriptDTO;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public enum Lang {
    JAVA(new JavaExecStrategyImpl()),
    PYTHON(new PythonExecStrategy()),
    SHELL(new ShellExecStrategy());

    private final ExecStrategy execStrategy;

    Lang(ExecStrategy execStrategy) {
        this.execStrategy = execStrategy;
    }

    public static ExecStrategy getExecStrategyForScript(ScriptDTO scriptDTO) {
        return scriptDTO.lang.execStrategy.getForScript(scriptDTO);
    }
}
