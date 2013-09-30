package net.dmi3.scriptexec.service.impl;

import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.service.Keeper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class MemoryBasedKeeperTest {


    Keeper keeper;

    @Before
    public void before() {
        keeper = new MemoryBasedKeeper();
    }


    @Test
    public void testPutGet() throws Exception {
        ScriptDTO scriptDTO = new ScriptDTO();

        Long id = keeper.put(scriptDTO);
        ScriptDTO result = keeper.get(id);

        assertEquals(scriptDTO, result);
    }

    @Test
    public void testReplace() throws Exception {
        ScriptDTO scriptDTO = new ScriptDTO();
        ScriptDTO scriptCopy = scriptDTO.copy();
        scriptCopy.result = "result";
        Long id = keeper.put(scriptDTO);
        scriptCopy.id = id;
        keeper.replace(scriptCopy);

        ScriptDTO result = keeper.get(id);

        assertEquals(scriptCopy, result);
    }
}
