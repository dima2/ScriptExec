package net.dmi3.scriptexec.controller;

import java.io.Reader;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface MainController {
    String get(String target);

    String post(Reader reader);
}
