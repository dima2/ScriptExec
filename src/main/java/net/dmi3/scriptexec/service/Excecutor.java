package net.dmi3.scriptexec.service;

import net.dmi3.scriptexec.entity.ScriptDTO;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface Excecutor {
    Long submit(ScriptDTO script);
}
