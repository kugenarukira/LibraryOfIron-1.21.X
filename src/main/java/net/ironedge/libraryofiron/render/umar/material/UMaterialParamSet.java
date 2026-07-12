package net.ironedge.libraryofiron.render.umar.material;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class UMaterialParamSet {

    private final Map<String, Object> values = new HashMap<>();

    public UMaterialParamSet() {
    }

    public UMaterialParamSet(UMaterialParamSet other) {
        if (other != null) {
            this.values.putAll(other.values);
        }
    }

    public UMaterialParamSet copy() {
        return new UMaterialParamSet(this);
    }

    public UMaterialParamSet setFloat(String key, float value) {
        values.put(key, value);
        return this;
    }

    public UMaterialParamSet setInt(String key, int value) {
        values.put(key, value);
        return this;
    }

    public UMaterialParamSet setBoolean(String key, boolean value) {
        values.put(key, value);
        return this;
    }

    public UMaterialParamSet setColor(String key, int argb) {
        values.put(key, argb);
        return this;
    }

    public UMaterialParamSet setTexture(String key, ResourceLocation texture) {
        values.put(key, texture);
        return this;
    }

    public UMaterialParamSet set(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public float getFloat(String key, float fallback) {
        Object value = values.get(key);
        return value instanceof Number number ? number.floatValue() : fallback;
    }

    public int getInt(String key, int fallback) {
        Object value = values.get(key);
        return value instanceof Number number ? number.intValue() : fallback;
    }

    public boolean getBoolean(String key, boolean fallback) {
        Object value = values.get(key);
        return value instanceof Boolean bool ? bool : fallback;
    }

    public int getColor(String key, int fallback) {
        Object value = values.get(key);
        return value instanceof Number number ? number.intValue() : fallback;
    }

    public ResourceLocation getTexture(String key, ResourceLocation fallback) {
        Object value = values.get(key);
        return value instanceof ResourceLocation rl ? rl : fallback;
    }

    public <T> T get(String key, Class<T> type, T fallback) {
        Objects.requireNonNull(type, "type");
        Object value = values.get(key);
        return type.isInstance(value) ? type.cast(value) : fallback;
    }

    public void putAll(UMaterialParamSet other) {
        if (other != null) {
            values.putAll(other.values);
        }
    }

    public Map<String, Object> asUnmodifiableMap() {
        return Collections.unmodifiableMap(values);
    }
}