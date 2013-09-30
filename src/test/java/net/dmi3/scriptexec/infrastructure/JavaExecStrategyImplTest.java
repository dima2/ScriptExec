package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.StringContains.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class JavaExecStrategyImplTest implements Settings {
    final String DEFAULT_JAVA = "public class Test\n" +
            "{\n" +
            "}";

    @Test
    public void testGetFile() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.JAVA, DEFAULT_JAVA);
        java.id = 1L;
        JavaExecStrategyImpl strategy = new JavaExecStrategyImpl(java);

        String result = strategy.getFile();

        assertThat(result, containsString("public class J1"));
    }

    @Test
    public void testCmd() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.JAVA, DEFAULT_JAVA);
        java.id = 1L;
        JavaExecStrategyImpl strategy = new JavaExecStrategyImpl(java);

        List<String> commands = strategy.getCmd();

        assertThat(commands, is(Arrays.asList(
                "javac "+ COMPILATION_DIR + "/J1.java",
                "java -classpath " + COMPILATION_DIR + " J1")));
    }

    @Test
    public void testFileNames() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.JAVA, DEFAULT_JAVA);
        java.id = 1L;
        JavaExecStrategyImpl strategy = new JavaExecStrategyImpl(java);

        String result = strategy.getFileName();
        List<String> filesForCleanup = strategy.getFilesForCleanup();

        assertEquals(COMPILATION_DIR + "/J1.java", result);
        assertThat(filesForCleanup, is(Arrays.asList(
                COMPILATION_DIR + "/J1.java",
                COMPILATION_DIR + "/J1.class")));

    }
}
