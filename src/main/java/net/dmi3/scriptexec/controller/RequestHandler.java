package net.dmi3.scriptexec.controller;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public interface RequestHandler extends org.eclipse.jetty.server.Handler {
    String EVAL = "/eval";
    String GET = "GET";
    String POST = "POST";
}
