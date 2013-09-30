package net.dmi3.scriptexec.service.impl;

import com.google.inject.Singleton;
import net.dmi3.scriptexec.entity.ScriptDTO;
import net.dmi3.scriptexec.service.Keeper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="http://dmi3.net">Dmitry</a>
 */

@Singleton
public class MemoryBasedKeeper implements Keeper {
    private AtomicLong i = new AtomicLong();
    protected Map<Long, ScriptDTO> storage = new ConcurrentHashMap<>();

    @Override
    public ScriptDTO get(Long id) {
        return storage.get(id);
    }

    @Override
    public Long put(ScriptDTO script) {
        script.id = i.get();
        storage.put(i.get(), script);
        return i.getAndIncrement();
    }

    @Override
    public void replace(ScriptDTO scriptCopy) {
        storage.put(scriptCopy.id, scriptCopy);
    }
}
