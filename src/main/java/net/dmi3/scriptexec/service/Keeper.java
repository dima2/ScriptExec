package net.dmi3.scriptexec.service;

import net.dmi3.scriptexec.entity.ScriptDTO;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface Keeper {
    ScriptDTO get(Long id);

    Long put(ScriptDTO script);

    void replace(ScriptDTO scriptCopy);
}
