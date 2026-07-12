package net.ironedge.libraryofiron.render.umr.importdata;

import net.ironedge.libraryofiron.core.LoILog;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAssetRegistry;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class UMRMeshAssetManager {

    private UMRMeshAssetManager() {}

    public static void registerFromResource(ResourceLocation rl) {
        String path = "/assets/" + rl.getNamespace() + "/" + rl.getPath();

        try (InputStream in = UMRMeshAssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Mesh asset resource not found: " + path);
            }

            try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                UMRMeshAssetDef def = UMRMeshJsonLoader.loadDef(reader);
                MeshAsset asset = UMRMeshAssetFactory.build(def);
                MeshAssetRegistry.register(asset);
                LoILog.info("Registered UMR mesh asset: " + asset.id());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load UMR mesh asset from " + rl, e);
        }
    }
}