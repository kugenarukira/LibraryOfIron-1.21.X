package net.ironedge.libraryofiron.render.umr.mesh.debug;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDebugInstances;

import net.ironedge.libraryofiron.render.umr.importdata.UMRMeshAssetManager;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAssetRegistry;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.render.MeshRenderNode;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public final class MeshDebugContent {

    private static boolean installed = false;
    private static MeshInstance testInstance;

    private MeshDebugContent() {}

    public static void install(RenderEngine engine) {
        if (installed) return;

        UMRMeshAssetManager.registerFromResource(
                ResourceLocation.fromNamespaceAndPath("libraryofiron", "models/mesh/debug_panel.json")
        );

        MeshAsset asset = MeshAssetRegistry.get("debug_panel");
        if (asset == null) {

            return;
        }

        testInstance = new MeshInstance("debug_panel_instance", asset);
        testInstance.translation(new Vector3f(0f, 62f, 0f));

        engine.graph().addNode(new MeshRenderNode(
                testInstance,
                UMaterialDebugInstances.EMISSIVE_HOT
        ));

        installed = true;
    }

    public static MeshInstance testInstance() {
        return testInstance;
    }
}