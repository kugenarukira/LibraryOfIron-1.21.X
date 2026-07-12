package net.ironedge.libraryofiron.render.umr.mesh;

import java.util.HashMap;
import java.util.Map;

public final class MeshAssetRegistry {

    private static final Map<String, MeshAsset> ASSETS = new HashMap<>();

    private MeshAssetRegistry() {}

    public static void register(MeshAsset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset must not be null");
        }
        ASSETS.put(asset.id(), asset);
    }

    public static MeshAsset get(String id) {
        return ASSETS.get(id);
    }

    public static boolean has(String id) {
        return ASSETS.containsKey(id);
    }

    public static void clear() {
        ASSETS.clear();
    }

    public static Map<String, MeshAsset> all() {
        return new HashMap<>(ASSETS);
    }
}