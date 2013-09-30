package net.dmi3.scriptexec;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.dmi3.scriptexec.config.ScriptExecModule;
import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.controller.RequestHandler;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.net.URL;

public class EntryPoint implements Settings {
    private static Logger LOG = Logger.getLogger(EntryPoint.class.getName());

    public static void main(String[] args) throws Exception {
        Server server = initServer(args);
        server.join();
    }

    protected static Server initServer(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new ScriptExecModule());

        HandlerList handlers = new HandlerList();
        handlers.setParallelStart(false);
        handlers.setHandlers(new Handler[]{getStaticResourceHandler(), injector.getInstance(RequestHandler.class)});

        int port = getPort(args);
        Server server = new Server(port);
        server.setHandler(handlers);
        server.start();
        LOG.info("Started server on http://localhost:" + port);
        return server;
    }

    private static ResourceHandler getStaticResourceHandler() {
        URL web = EntryPoint.class.getClassLoader().getResource("web");
        if (web == null) throw new IllegalStateException("Unable to find resource directory");
        String path = web.toExternalForm();

        ResourceHandler staticResourceHandler = new ResourceHandler();
        staticResourceHandler.setResourceBase(path);
        staticResourceHandler.setDirectoriesListed(true);
        staticResourceHandler.setWelcomeFiles(new String[]{"index.html"});

        return staticResourceHandler;
    }

    static int getPort(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOG.info("Using default port");
            }
        }
        return port;
    }
}
