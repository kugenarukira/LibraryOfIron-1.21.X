package net.ironedge.libraryofiron.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataRegistry {

    private final Map<Object, Object> registryMap = new ConcurrentHashMap<>();

    // register any value with a key
    public <T> void register(Object key, T value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null");
        }
        registryMap.put(key, value);
    }

    // get any value by key, generic
    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        return (T) registryMap.get(key);
    }
}
