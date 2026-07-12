package net.ironedge.libraryofiron.render.umr.skinning.debug;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDebugInstances;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAssetRegistry;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.render.MeshRenderNode;
import org.joml.Vector3f;

public final class SkinnedStripDebugContent {

    private static boolean installed = false;

    public static MeshInstance instance;
    public static SkinnedStripBuilder.Result result;

    private SkinnedStripDebugContent() {}

    public static void install(RenderEngine engine) {
        if (installed) return;
        installed = true;

        result = SkinnedStripBuilder.build(
                "debug_skinned_strip",
                "skinned_strip",
                12,
                2,
                0.45f,
                2.0f
        );

        MeshAssetRegistry.register(result.asset());

        instance = new MeshInstance("debug_skinned_strip_instance", result.asset());
        instance.translation(new Vector3f(3f, 62f, 0f));
        instance.scale(new Vector3f(3f, 3f, 3f));
        instance.skeletonPose(result.pose());

        //System.out.println("[UMR SKIN TEST] adding render node + pose node");

        engine.graph().addNode(new MeshRenderNode(
                instance,
                UMaterialDebugInstances.FLAT_WHITE_INSTANCE
        ));

        engine.graph().addNode(new SkinnedStripPoseDebugNode());

    }
}