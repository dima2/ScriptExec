package net.dmi3.scriptexec.config;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface Settings {
    String COMPILATION_DIR = System.getProperty("java.io.tmpdir") + "/scriptExec";
    int DEFAULT_PORT = 8080;
    int TIMEOUT_SEC = 10;
}
