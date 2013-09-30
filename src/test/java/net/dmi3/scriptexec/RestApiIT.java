package net.dmi3.scriptexec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class RestApiIT {
    static final String PORT = "8081";
    private Server server;
    private HttpClient client;

    public static final Type jsonToHashMap = new TypeToken<Map<String, String>>(){}.getType();

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
    public void testSimpleEvalJson() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
            post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'echo 1'}", "application/json", "utf-8"));
        int statusCode = client.executeMethod(post);

        assertEquals(HttpStatus.SC_OK, statusCode);

        Gson gson = new Gson();
        HashMap<String, String> json = gson.fromJson(post.getResponseBodyAsString(), jsonToHashMap);

        assertEquals("0",json.get("id"));
            assertEquals("PENDING", json.get("status"));

        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/" + 0);
        statusCode = client.executeMethod(get);
        json = gson.fromJson(get.getResponseBodyAsString(), jsonToHashMap);

        assertEquals(HttpStatus.SC_OK, statusCode);
        assertEquals("0", json.get("id"));
        assertEquals("EXCECUTED",json.get("status"));
        assertEquals("SHELL", json.get("lang"));
        assertEquals("echo 1", json.get("script"));
        assertEquals("1", json.get("result").trim());
    }

    static Integer CNT = 10;

    @Test
    public void testMultiEval() throws Exception {
        for (int i = 0; i < CNT; i++) {
            PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
            post.setRequestEntity(new StringRequestEntity("{'lang':'SHELL','script':'echo " + i + "'}", "application/json", "utf-8"));
            client.executeMethod(post);
        }

        Gson gson = new Gson();

        for (int i = 0; i < CNT; i++) {
            GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/" + i);
            client.executeMethod(get);

            ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);
            assertNotNull(scriptDTO);
            assertEquals("" + i, scriptDTO.result.trim());
        }
    }


    @Test
    public void testGetNotExistingObject() throws Exception {
        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/999");
        int statusCode = client.executeMethod(get);

        Gson gson = new Gson();
        ScriptDTO scriptDTO = gson.fromJson(get.getResponseBodyAsString(), ScriptDTO.class);

        assertEquals(HttpStatus.SC_OK, statusCode);
        assertEquals(scriptDTO.status, Status.NOT_FOUND);
    }

    @Test
    public void testFileServer() throws Exception {
        GetMethod get = new GetMethod("http://localhost:" + PORT + "/");
        int statusCode = client.executeMethod(get);

        assertThat(get.getResponseBodyAsString(), containsString("<html>"));
        assertEquals(HttpStatus.SC_OK, statusCode);
    }

    @Test
    public void testNotExistingPage() throws Exception {
        GetMethod get = new GetMethod("http://localhost:" + PORT + "/wrong");
        int statusCode = client.executeMethod(get);

        assertEquals(HttpStatus.SC_NOT_FOUND, statusCode);
    }

    @Test
    public void testPostInvalidObject() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'wrong':'object'}", "application/json", "utf-8"));
        int statusCode = client.executeMethod(post);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }

    @Test
    public void testPostInvalidJson() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("{'wrong':'objec...", "application/json", "utf-8"));
        int statusCode = client.executeMethod(post);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }

    @Test
    public void testPostNotJson() throws Exception {
        PostMethod post = new PostMethod("http://localhost:" + PORT + "/eval");
        post.setRequestEntity(new StringRequestEntity("NOT_JSON", "application/json", "utf-8"));
        int statusCode = client.executeMethod(post);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }

    @Test
    public void testGetInvalidNumber() throws Exception {
        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval/I");
        int statusCode = client.executeMethod(get);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }

    @Test
    public void testGetInvalidUrl() throws Exception {
        GetMethod get = new GetMethod("http://localhost:" + PORT + "/eval");
        int statusCode = client.executeMethod(get);

        assertEquals(HttpStatus.SC_BAD_REQUEST, statusCode);
    }
}
