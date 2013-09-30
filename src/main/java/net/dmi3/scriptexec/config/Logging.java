package net.dmi3.scriptexec.config;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class Logging {
    public Logging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }
}
