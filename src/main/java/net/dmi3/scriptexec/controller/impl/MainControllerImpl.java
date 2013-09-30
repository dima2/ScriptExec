package net.dmi3.scriptexec.controller.impl;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dmi3.scriptexec.controller.MainController;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.entity.Status;
import net.dmi3.scriptexec.service.Excecutor;
import net.dmi3.scriptexec.service.Keeper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Reader;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

@Singleton
public class MainControllerImpl implements MainController {
    @Inject
    private Excecutor excecutor;

    @Inject
    private Keeper keeper;

    private static Logger LOG = Logger.getLogger(RequestHandlerImpl.class.getName());

    @Override
    public String get(String target) {
        Gson gson = new Gson();

        Long id;

        try {
            id = Long.valueOf(target.substring(1));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            LOG.error("Got invalid request");
            return null;
        }

        ScriptDTO scriptDTO = keeper.get(id);

        if (scriptDTO == null) {
            scriptDTO = new ScriptDTO();
            scriptDTO.id = id;
            scriptDTO.status = Status.NOT_FOUND;
        }

        return gson.toJson(scriptDTO);
    }

    @Override
    public String post(Reader body) {
        Gson gson = new Gson();

        ScriptDTO scriptDTO = null;
        try {
            scriptDTO = gson.fromJson(body, ScriptDTO.class);
        } catch (JsonParseException e) {
            LOG.error("Got wrong JSON ");
        }

        if (scriptDTO == null || scriptDTO.lang == null || StringUtils.isEmpty(scriptDTO.script)) return null;

        scriptDTO.status = Status.PENDING;
        Long id = excecutor.submit(scriptDTO);

        ScriptDTO response = new ScriptDTO();
        response.id = id;
        response.status = Status.PENDING;

        return gson.toJson(response);
    }


}
