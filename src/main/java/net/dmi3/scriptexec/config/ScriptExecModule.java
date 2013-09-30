package net.dmi3.scriptexec.config;

import com.google.inject.AbstractModule;
import net.dmi3.scriptexec.controller.MainController;
import net.dmi3.scriptexec.controller.RequestHandler;
import net.dmi3.scriptexec.controller.impl.MainControllerImpl;
import net.dmi3.scriptexec.controller.impl.RequestHandlerImpl;
import net.dmi3.scriptexec.service.Excecutor;
import net.dmi3.scriptexec.service.Keeper;
import net.dmi3.scriptexec.service.impl.AsyncOsExcecutor;
import net.dmi3.scriptexec.service.impl.MemoryBasedKeeper;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class ScriptExecModule extends AbstractModule {
    @Override
    protected void configure() {
//      bind(Excecutor.class).to(SimpleOsExcecutor.class);
        bind(Excecutor.class).to(AsyncOsExcecutor.class);

        bind(Keeper.class).to(MemoryBasedKeeper.class);
        bind(RequestHandler.class).to(RequestHandlerImpl.class);
        bind(Excecutor.class).to(AsyncOsExcecutor.class);
        bind(MainController.class).to(MainControllerImpl.class);

        bind(Logging.class).asEagerSingleton();
    }
}
