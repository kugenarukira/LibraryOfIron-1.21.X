package net.ironedge.libraryofiron.capability;

import net.ironedge.libraryofiron.core.LoIContext;

import java.util.HashMap;
import java.util.Map;

public final class CapabilityContainer {

    private final Map<CapabilityKey<?>, LoICapability<?>> capabilities = new HashMap<>();

    public <T> void add(CapabilityKey<T> key, LoICapability<T> capability) {
        if (capabilities.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate capability: " + key);
        }
        capabilities.put(key, capability);
    }

    @SuppressWarnings("unchecked")
    public <T> LoICapability<T> get(CapabilityKey<T> key) {
        return (LoICapability<T>) capabilities.get(key);
    }

    public boolean has(CapabilityKey<?> key) {
        return capabilities.containsKey(key);
    }

    public void tickAll(LoIContext context, Object holder) {
        capabilities.values().forEach(cap -> cap.tick(context, holder));
    }
}