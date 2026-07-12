package net.ironedge.libraryofiron.render.umar.material;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class UMaterialRegistry {

    private static final Map<ResourceLocation, UMaterialDefinition> MATERIALS = new HashMap<>();

    private UMaterialRegistry() {
    }

    public static void register(UMaterialDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        MATERIALS.put(definition.id(), definition);
    }

    public static UMaterialDefinition get(ResourceLocation id) {
        return MATERIALS.get(id);
    }

    public static UMaterialDefinition require(ResourceLocation id) {
        UMaterialDefinition definition = get(id);
        if (definition == null) {
            throw new IllegalStateException("No UMaterialDefinition registered for id: " + id);
        }
        return definition;
    }

    public static boolean contains(ResourceLocation id) {
        return MATERIALS.containsKey(id);
    }

    public static Map<ResourceLocation, UMaterialDefinition> all() {
        return Collections.unmodifiableMap(MATERIALS);
    }

    public static void clear() {
        MATERIALS.clear();
    }
}