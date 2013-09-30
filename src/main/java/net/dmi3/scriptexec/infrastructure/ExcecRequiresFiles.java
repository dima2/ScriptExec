package net.dmi3.scriptexec.infrastructure;

import java.util.List;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface ExcecRequiresFiles {
    String getFile();

    String getFileName();

    List<String> getFilesForCleanup();
}
