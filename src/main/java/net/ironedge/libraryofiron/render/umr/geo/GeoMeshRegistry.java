package net.ironedge.libraryofiron.render.umr.geo;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GeoMeshRegistry {
    private static final Map<ResourceLocation, GeoMesh> CACHE = new ConcurrentHashMap<>();

    private GeoMeshRegistry() {}

    /**
     * @param geoId points to assets/<modid>/models/geo/<path>.geo.json
     * Example: new ResourceLocation(modid, "models/geo/example.geo.json")
     */
    public static GeoMesh get(ResourceLocation geoId) {
        return CACHE.computeIfAbsent(geoId, GeoMeshRegistry::loadAndBake);
    }

    public static void clear() {
        CACHE.clear();
    }

    private static GeoMesh loadAndBake(ResourceLocation geoId) {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            Resource res = rm.getResourceOrThrow(geoId);

            try (var in = res.open();
                 var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {

                GeoModel model = GeoJsonLoader.load(reader);
                return GeoBaker.bake(model);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load geo json: " + geoId, e);
        }
    }
}