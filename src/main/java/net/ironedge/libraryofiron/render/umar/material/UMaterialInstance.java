package net.ironedge.libraryofiron.render.umar.material;

import java.util.Objects;

public final class UMaterialInstance {

    private final UMaterialDefinition definition;
    private final UMaterialParamSet overrides = new UMaterialParamSet();

    public UMaterialInstance(UMaterialDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
    }

    public UMaterialDefinition definition() {
        return definition;
    }

    public UMaterialParamSet overrides() {
        return overrides;
    }

    public UMaterialInstance setFloat(String key, float value) {
        overrides.setFloat(key, value);
        return this;
    }

    public UMaterialInstance setInt(String key, int value) {
        overrides.setInt(key, value);
        return this;
    }

    public UMaterialInstance setBoolean(String key, boolean value) {
        overrides.setBoolean(key, value);
        return this;
    }

    public UMaterialInstance setColor(String key, int argb) {
        overrides.setColor(key, argb);
        return this;
    }

    public UMaterialInstance set(String key, Object value) {
        overrides.set(key, value);
        return this;
    }

    public UMaterialInstance materialInstance() {
        return null;
    }
}