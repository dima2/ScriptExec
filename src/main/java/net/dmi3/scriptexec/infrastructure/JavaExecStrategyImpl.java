package net.dmi3.scriptexec.infrastructure;

import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class JavaExecStrategyImpl implements ExecStrategy, ExcecRequiresFiles, Settings {
    private ScriptDTO scriptDTO;

    //Constructor for net.dmi3.scriptexec.infrastructure.Lang
    JavaExecStrategyImpl() {
    }

    public JavaExecStrategyImpl(ScriptDTO scriptDTO) {
        this.scriptDTO = scriptDTO;
    }

    @Override
    public ExecStrategy getForScript(ScriptDTO scriptDTO) {
        return new JavaExecStrategyImpl(scriptDTO);
    }
    @Override
    public String getFile() {
        String script = scriptDTO.script;
        script = script.replaceFirst("(public\\s+?class\\s+?)[A-Za-z]+", "$1J" + scriptDTO.id);

        return script;
    }

    @Override
    public String getFileName() {
        return COMPILATION_DIR + "/J" + scriptDTO.id + ".java";
    }

    @Override
    public List<String> getFilesForCleanup() {
        return Arrays.asList(getFileName(), COMPILATION_DIR + "/J" + scriptDTO.id + ".class");
    }

    @Override
    public List<String> getCmd() {
        return Arrays.asList("javac " + getFileName(), "java -classpath " + COMPILATION_DIR + " J" + scriptDTO.id);
    }
}
