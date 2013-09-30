package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.entity.ScriptDTO;

import java.util.List;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface ExecStrategy {
    ExecStrategy getForScript(ScriptDTO scriptDTO);

    List<String> getCmd();
}
