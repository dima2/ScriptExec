package net.dmi3.scriptexec.controller.impl;

import net.dmi3.scriptexec.controller.MainController;
import net.dmi3.scriptexec.controller.RequestHandler;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;

import static org.mockito.Mockito.*;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class RequestHandlerImplTest {
    @Mock
    MainController mainController;

    @InjectMocks
    RequestHandler requestHandler = new RequestHandlerImpl();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPost() throws Exception {
        //Setup
        BufferedReader reader = mock(BufferedReader.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn(reader).when(request).getReader();
        doReturn("POST").when(request).getMethod();

        final String output = "OUTPUT";
        doReturn(output).when(mainController).post(reader);

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/eval", mock(Request.class), request, response);

        //Check
        verify(mainController).post(reader);
        verify(writer).println(output);
    }

    @Test
    public void testUnsupportedUrlPost() throws Exception {
        //Setup
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("POST").when(request).getMethod();

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/wrongUrl", mock(Request.class), request, response);

        //Check
        verify(mainController, never()).post(any(Reader.class));
        verify(writer, never()).println(anyString());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    public void testErrorInPost() throws Exception {
        //Setup
        BufferedReader reader = mock(BufferedReader.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn(reader).when(request).getReader();
        doReturn("POST").when(request).getMethod();

        doReturn(null).when(mainController).post(reader);

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/eval", mock(Request.class), request, response);

        //Check
        verify(mainController).post(reader);
        verify(response).setStatus(400);
        verify(writer, never()).println(anyString());
    }

    @Test
    public void testGet() throws Exception {
        //Setup
        final String output = "OUTPUT";
        final String target = "/1";
        doReturn(output).when(mainController).get(target);

        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("GET").when(request).getMethod();

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/eval" + target, mock(Request.class), request, response);

        //Check
        verify(mainController).get(target);
        verify(writer).println(output);
    }

    @Test
    public void testUnsupportedUrlGet() throws Exception {
        //Setup

        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("GET").when(request).getMethod();

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/wrongUrl", mock(Request.class), request, response);

        //Check
        verify(mainController, never()).get(anyString());
        verify(writer, never()).println(anyString());
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    public void testErrorGet() throws Exception {
        //Setup
        final String target = "/1";
        doReturn(null).when(mainController).get(target);

        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("GET").when(request).getMethod();

        PrintWriter writer = mock(PrintWriter.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        doReturn(writer).when(response).getWriter();

        //Run
        requestHandler.handle("/eval" + target, mock(Request.class), request, response);

        //Check
        verify(mainController).get(target);
        verify(writer, never()).println(anyString());
        verify(response).setStatus(400);
    }
}
