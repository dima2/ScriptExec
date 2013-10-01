package net.dmi3.scriptexec;

import com.google.gson.Gson;
import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.entity.Status;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class FunctionalityIT implements Settings {
    static final String PORT = "8081";
    private Server server;
    private HttpClient client;

    @Before
    public void before() {
        try {
            server = EntryPoint.initServer(new String[]{PORT});
        } catch (Exception e) {
            fail("Unable to start server " + ExceptionUtils.getStackTrace(e));
        }
        client = new HttpClient();
    }

    @After
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            fail("Unable to stop server " + ExceptionUtils.getStackTrace(e));
        }
    }


    @Test
    public void testShell() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'echo 1'}", "application/json", "utf-8"));
        client.executeMethod(post);


        Gson gson = new Gson();


        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals("1", scriptDTO.result.trim());

    }

    @Test
    public void testMultiLine() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'echo 1 && echo 2'}", "application/json", "utf-8"));
        client.executeMethod(post);


        Gson gson = new Gson();


        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals("1\n2", scriptDTO.result.trim());

    }

    @Test
    public void testPython() throws Exception {
        int fileCnt = new File(COMPILATION_DIR).list().length;
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'PYTHON','script':'print(1+2)'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Gson gson = new Gson();

        Thread.sleep(1000);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals("3", scriptDTO.result.trim());
        assertEquals(fileCnt, new File(COMPILATION_DIR).list().length);

    }

    @Test
    public void testJava() throws Exception {
        int fileCnt = new File(COMPILATION_DIR).list().length;
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");

        String script = "public class Test\n" +
                "{\n" +
                "public static void main (String[] args) throws java.lang.Exception\n" +
                "{\n" +
                "System.out.println(1+2);\n" +
                "}\n" +
                "}";

        post.setRequestEntity(new StringRequestEntity("{'lang':'JAVA','script':'" + script + "'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Gson gson = new Gson();

        Thread.sleep(1000);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals("3", scriptDTO.result.trim());
        assertEquals(fileCnt, new File(COMPILATION_DIR).list().length);
    }

    @Test
    public void testLongScript() throws Exception {
        Gson gson = new Gson();

        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'sleep 1 && echo 1'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Thread.sleep(100);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);
        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);

        assertNotNull(scriptDTO);
        assertEquals(Status.PENDING, scriptDTO.status);

        Thread.sleep(2000);

        client.executeMethod(get);
        scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals(Status.EXCECUTED, scriptDTO.status);
        assertEquals("1", scriptDTO.result.trim());
    }

    @Test
    public void testTooLongScript() throws Exception {
        Gson gson = new Gson();

        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'sleep 100'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Thread.sleep((TIMEOUT_SEC+1)*1000);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);
        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals(Status.TIMEOUT, scriptDTO.status);
    }

    @Test
    public void testUnknownScript() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'lang':'ArmyScript','script':'I command you to do stuff!'}", "application/json", "utf-8"));
        int statusCode = client.executeMethod(post);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);

    }

    @Test
    public void testInvalidScript() throws Exception {
        int fileCnt = new File(COMPILATION_DIR).list().length;
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");

        String script = "public class Test\n" +
                "{\n" +
                "public static void main (String[] args) throws java.lang.Exception\n" +
                "{\n" +
                "((Object) null).toString();\n" +
                "}\n" +
                "}";

        post.setRequestEntity(new StringRequestEntity("{'lang':'JAVA','script':'" + script + "'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Gson gson = new Gson();

        Thread.sleep(1000);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals(Status.SCRIPT_ERROR, scriptDTO.status);
        assertThat(scriptDTO.result, containsString("java.lang.NullPointerException"));
        assertEquals(fileCnt, new File(COMPILATION_DIR).list().length);
    }

    @Test
    public void testNotScript() throws Exception {
        int fileCnt = new File(COMPILATION_DIR).list().length;
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");

        post.setRequestEntity(new StringRequestEntity("{'lang':'JAVA','script':'This clearly does not look like Java'}", "application/json", "utf-8"));
        client.executeMethod(post);

        Gson gson = new Gson();

        Thread.sleep(1000);

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/0");
        client.executeMethod(get);

        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
        assertNotNull(scriptDTO);
        assertEquals(Status.SCRIPT_ERROR, scriptDTO.status);
        assertEquals(fileCnt, new File(COMPILATION_DIR).list().length);
    }
}
