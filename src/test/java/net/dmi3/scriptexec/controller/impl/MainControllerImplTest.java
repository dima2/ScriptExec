package net.dmi3.scriptexec.controller.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dmi3.scriptexec.controller.MainController;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.entity.Status;
import net.dmi3.scriptexec.infrastructure.Lang;
import net.dmi3.scriptexec.service.Excecutor;
import net.dmi3.scriptexec.service.Keeper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.CharArrayReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class MainControllerImplTest {
    @Mock
    Keeper keeper;

    @Mock
    Excecutor excecutor;

    @InjectMocks
    MainController controller = new MainControllerImpl();

    static final Type jsonToHashMap = new TypeToken<Map<String, String>>(){}.getType();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPost() throws Exception {
        //Setup
        ScriptDTO template = new ScriptDTO(Lang.SHELL, "echo 1");

        //Run
        String result = controller.post(new CharArrayReader(String.format("{'lang':'%s','script':'%s'}", template.lang, template.script).toCharArray()));

        //Check
        Gson gson = new Gson();
        HashMap<String, String> json = gson.fromJson(result, jsonToHashMap);
        template.status = Status.PENDING;

        verify(excecutor).submit(template);
        assertEquals("0", json.get("id"));
        assertEquals("PENDING",json.get("status"));
    }

    @Test
    public void testPostInvalidJson() throws Exception {
        String result = controller.post(new CharArrayReader("{'hello':'work','test':'test'}".toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostNotJson() throws Exception {
        String result = controller.post(new CharArrayReader("not json".toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostEmptyScript() throws Exception {
        String result = controller.post(new CharArrayReader(String.format("{'lang':'%s','script':''}", Lang.JAVA.name()).toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostNoScript() throws Exception {
        String result = controller.post(new CharArrayReader(String.format("{'lang':'%s'}", Lang.JAVA.name()).toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostEmptyLang() throws Exception {
        String result = controller.post(new CharArrayReader("{'lang':'','script':'echo 1'}".toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostNoLang() throws Exception {
        String result = controller.post(new CharArrayReader("{'script':'echo 1'}".toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }

    @Test
    public void testPostUnknownLang() throws Exception {
        String result = controller.post(new CharArrayReader("{'lang':'UNKNOWN','script':'echo 1'}".toCharArray()));

        verify(excecutor, never()).submit(any(ScriptDTO.class));
        assertNull(result);
    }


    private ScriptDTO getDefaultScriptDTO(Long ID) {
        ScriptDTO scriptDTO = new ScriptDTO(Lang.SHELL, "echo 1");
        scriptDTO.id = ID;
        scriptDTO.status = Status.PENDING;
        scriptDTO.result = "result";

        return scriptDTO;
    }

    @Test
    public void testGet() throws Exception {
        //Setup
        final Long ID = 1L;
        ScriptDTO scriptDTO = getDefaultScriptDTO(ID);
        doReturn(scriptDTO).when(keeper).get(ID);

        //Run
        String result = controller.get("/"+ID);

        //Check
        Gson gson = new Gson();
        HashMap<String, String> json = gson.fromJson(result, jsonToHashMap);

        assertEquals(ID.toString(), json.get("id"));
        assertEquals(scriptDTO.status.toString(),json.get("status"));
        assertEquals(scriptDTO.script,json.get("script"));
        assertEquals(scriptDTO.result,json.get("result"));
    }

    @Test
    public void testGetWrongUrl1() throws Exception {
        doReturn(getDefaultScriptDTO(1L)).when(keeper).get(1L);

        String result = controller.get("/");

        assertNull(result);
    }

    @Test
    public void testGetWrongUrl2() throws Exception {
        doReturn(getDefaultScriptDTO(1L)).when(keeper).get(1L);

        String result = controller.get("");

        assertNull(result);
    }

    @Test
    public void testGetWrongUrl3() throws Exception {
        doReturn(getDefaultScriptDTO(1L)).when(keeper).get(1L);

        String result = controller.get("/one");

        assertNull(result);
    }

    @Test
    public void testGetNotExistingScript() throws Exception {
        final Long ID = 1L;
        String result = controller.get("/"+ID);

        Gson gson = new Gson();
        HashMap<String, String> json = gson.fromJson(result, jsonToHashMap);

        assertEquals(ID.toString(), json.get("id"));
        assertEquals(Status.NOT_FOUND.toString(),json.get("status"));
    }
}
