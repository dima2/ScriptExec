package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.entity.ScriptDTO;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class ShellExecStrategyTest {
    @Test
    public void testGetCmd() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.SHELL, "print 10");
        ShellExecStrategy strategy = new ShellExecStrategy(java);

        List<String> cmd = strategy.getCmd();

        assertThat(cmd, is(Arrays.asList(
            "print 10"
        )));
    }

    @Test
    public void testGetCmdMulti1() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.SHELL, "sleep 10 && print 10");
        ShellExecStrategy strategy = new ShellExecStrategy(java);

        List<String> cmd = strategy.getCmd();

        assertThat(cmd, is(Arrays.asList(
                "sleep 10",
                "print 10"
        )));
    }

    @Test
    public void testGetCmdMulti2() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.SHELL, "sleep 10  && print 10");
        ShellExecStrategy strategy = new ShellExecStrategy(java);

        List<String> cmd = strategy.getCmd();

        assertThat(cmd, is(Arrays.asList(
                "sleep 10",
                "print 10"
        )));
    }

    @Test
    public void testGetCmdMulti3() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.SHELL, "sleep 10&&print 10");
        ShellExecStrategy strategy = new ShellExecStrategy(java);

        List<String> cmd = strategy.getCmd();

        assertThat(cmd, is(Arrays.asList(
                "sleep 10",
                "print 10"
        )));
    }

    @Test
    public void testGetCmdMulti3cmds() throws Exception {
        ScriptDTO java = new ScriptDTO(Lang.SHELL, "sleep 10 && print 10 && print 20");
        ShellExecStrategy strategy = new ShellExecStrategy(java);

        List<String> cmd = strategy.getCmd();

        assertThat(cmd, is(Arrays.asList(
                "sleep 10",
                "print 10",
                "print 20"
        )));
    }
}
