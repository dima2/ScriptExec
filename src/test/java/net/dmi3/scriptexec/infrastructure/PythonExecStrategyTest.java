package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class PythonExecStrategyTest implements Settings {
    @Test
    public void testFileNames() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.PYTHON, "print(10)");
        java.id=1L;
        PythonExecStrategy strategy = new PythonExecStrategy(java);

        String result = strategy.getFileName();
        List<String> filesForCleanup = strategy.getFilesForCleanup();

        assertEquals(COMPILATION_DIR+"/1.py", result);
        assertThat(filesForCleanup, hasItem(COMPILATION_DIR+"/1.py"));
        assertThat(filesForCleanup, hasItem(COMPILATION_DIR+"/1.pyc"));
    }
}
