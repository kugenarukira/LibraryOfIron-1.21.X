package net.ironedge.libraryofiron.core;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.event.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoIContext {

    private final List<LoIModule> modules = new ArrayList<>();
    private final Map<CapabilityKey<?>, Object> dataRegistry = new HashMap<>();
    private final Map<Object, CapabilityContainer> entityCapabilities = new HashMap<>();
    private final EventBus eventBus = new EventBus();

    /** DATA REGISTRY **/

    public <T> void registerData(CapabilityKey<T> key, T instance) {
        dataRegistry.put(key, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(CapabilityKey<T> key) {
        return (T) dataRegistry.get(key);
    }

    /** ENTITY CAPABILITIES **/

    public CapabilityContainer getCapabilityContainer(Object entity) {
        return entityCapabilities.computeIfAbsent(entity, k -> new CapabilityContainer());
    }

    /** EVENT BUS **/

    public EventBus getEventBus() {
        return eventBus;
    }

    /** MODULE SYSTEM **/

    public void registerModule(LoIModule module) {
        modules.add(module);
        module.onRegister(this);
    }

    void fireCommonSetup() {
        modules.forEach(m -> m.onCommonSetup(this));
    }

    void fireClientSetup() {
        modules.forEach(m -> m.onClientSetup(this));
    }

    void fireServerSetup() {
        modules.forEach(m -> m.onServerSetup(this));
    }

    public void shutdown() {
        modules.forEach(m -> m.onShutdown(this));
    }
}