package net.dmi3.scriptexec.controller.impl;

import com.google.inject.Inject;
import net.dmi3.scriptexec.controller.MainController;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class RequestHandlerImpl extends AbstractHandler implements net.dmi3.scriptexec.controller.RequestHandler {
    @Inject
    private MainController mainController;

    private static Logger LOG = Logger.getLogger(RequestHandlerImpl.class.getName());

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String output = null;

        if (!target.startsWith(EVAL)) {
            LOG.info("Skipping url " + target);
            return;
        }

        if (GET.equals(request.getMethod())) {
            String req = target.substring(EVAL.length());
            output = mainController.get(req);
        } else if (POST.equals(request.getMethod())) {
            output = mainController.post(request.getReader());
        }

        response.setContentType("text/html;charset=utf-8");
        baseRequest.setHandled(true);

        if (output == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.getWriter().println(output);
        }
    }
}
