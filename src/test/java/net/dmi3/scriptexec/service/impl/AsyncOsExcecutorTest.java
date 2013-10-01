package net.dmi3.scriptexec.service.impl;

import net.dmi3.scriptexec.config.Settings;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.entity.Status;
import net.dmi3.scriptexec.infrastructure.ExcecRequiresFiles;
import net.dmi3.scriptexec.infrastructure.Lang;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

public class AsyncOsExcecutorTest implements Settings {
    @Spy
    MemoryBasedKeeper keeper = new MemoryBasedKeeper();

    @InjectMocks
    AsyncOsExcecutor asyncOsExcecutor = spy(new AsyncOsExcecutor());

    @Before
    public void initMocks() throws Exception  {
        MockitoAnnotations.initMocks(this);
        doNothing().when(asyncOsExcecutor).writeFile(any(ExcecRequiresFiles.class));
        doNothing().when(asyncOsExcecutor).removeFiles(any(ExcecRequiresFiles.class));
    }

    static Integer CNT = 10;

    @Test
    public void testSubmit() throws Exception {
        //Setup
        Runtime runtime = mock(Runtime.class);
        asyncOsExcecutor.runtime = runtime;

        List<String> scripts = new ArrayList<>();

        for (int i = 0; i < CNT; i++) {
            String script = "echo " + i;
            scripts.add(script);

            Process process = mock(Process.class);
            doReturn(IOUtils.toInputStream(script)).when(process).getInputStream();
            doReturn(process).when(runtime).exec(script);
        }

        //Run
        for (String script : scripts) {
            asyncOsExcecutor.submit(new ScriptDTO(Lang.SHELL, script));
        }

        asyncOsExcecutor.exec.shutdown();
        asyncOsExcecutor.exec.awaitTermination(10, TimeUnit.SECONDS);

        //Check
        Collection<Object> result = new ArrayList<Object>(keeper.storage.values());
        for (String script : scripts) {
            assertThat(result, hasItem(hasProperty("result", is(script))));
        }
    }

    @Test
    public void testUpdater() throws Exception {
        final long ID = 1L;
        ScriptDTO scriptDTO = new ScriptDTO(Lang.SHELL, "echo 1");
        scriptDTO.id = ID;
        FutureTask excecutorTask = mock(FutureTask.class);
        doReturn(scriptDTO).when(excecutorTask).get(TIMEOUT_SEC, TimeUnit.SECONDS);

        AsyncOsExcecutor.Updater updater = asyncOsExcecutor.new Updater(ID, keeper, excecutorTask);
        updater.run();

        verify(keeper).replace(eq(scriptDTO));
    }

    @Test
    public void testUpdaterTimeout() throws Exception {
        final long ID = 1L;
        ScriptDTO scriptDTO = new ScriptDTO(Lang.SHELL, "echo 1");
        scriptDTO.id = ID;
        FutureTask excecutorTask = mock(FutureTask.class);
        ScriptDTO template = scriptDTO.copy();
        doReturn(template).when(keeper).get(ID);
        doThrow(new TimeoutException()).when(excecutorTask).get(TIMEOUT_SEC, TimeUnit.SECONDS);

        AsyncOsExcecutor.Updater updater = asyncOsExcecutor.new Updater(ID, keeper, excecutorTask);
        updater.run();

        template.status = Status.TIMEOUT;
        verify(keeper).replace(eq(template));
        verify(excecutorTask).cancel(true);
    }

    @Test
    public void testUpdaterNotExistingScript() throws Exception {
        final long ID = 1L;
        ScriptDTO scriptDTO = new ScriptDTO(Lang.SHELL, "echo 1");
        scriptDTO.id = ID;
        FutureTask excecutorTask = mock(FutureTask.class);
        doThrow(new ExecutionException(new Throwable())).when(excecutorTask).get(TIMEOUT_SEC, TimeUnit.SECONDS);

        AsyncOsExcecutor.Updater updater = asyncOsExcecutor.new Updater(ID, keeper, excecutorTask);
        updater.run();

        ScriptDTO template = new ScriptDTO();
        template.id = ID;
        template.status = Status.SCRIPT_ERROR;
        verify(keeper).replace(eq(template));
    }

}
