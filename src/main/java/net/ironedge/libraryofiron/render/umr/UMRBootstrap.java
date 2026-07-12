package net.ironedge.libraryofiron.render.umr;

import net.ironedge.libraryofiron.render.anchor.AnchorType;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorProviders;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolverRegistry;
import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.debug.BridgeDebugNode;
import net.ironedge.libraryofiron.render.debug.GeoDebugContent;
import net.ironedge.libraryofiron.render.debug.UMRDebugNode;
import net.ironedge.libraryofiron.render.bridge.BridgeSolveNode;
import net.ironedge.libraryofiron.render.umr.importdata.fbx.FbxDebugContent;
import net.ironedge.libraryofiron.render.umr.mesh.debug.MeshDebugContent;
import net.ironedge.libraryofiron.render.umr.skinning.debug.SkinnedStripDebugContent;

public final class UMRBootstrap {
    private static boolean installed = false;

    public static void install(RenderEngine engine) {
        // 1) Create resolver instances ONCE
        StaticAnchorResolver staticResolver = new StaticAnchorResolver();
        DynamicAnchorResolver dynamicResolver = new DynamicAnchorResolver();

        // 2) Register them in the registry ONCE
        AnchorResolverRegistry.registerResolver(AnchorType.STATIC, staticResolver);
        AnchorResolverRegistry.registerResolver(AnchorType.DYNAMIC, dynamicResolver);

        // 3) Register PoseGraph-backed dynamic anchors ONCE (providers)
        // (Implementation shown below)
        DynamicAnchorProviders.installPoseGraphProviders(dynamicResolver);
        if (installed) return;
        installed = true;

        engine.graph().addNode(new UMRPosePublishNode());
        engine.graph().addNode(new UMRDebugNode());
        engine.graph().addNode(new BridgeSolveNode());
        engine.graph().addNode(new BridgeDebugNode());

        // Everything “contenty” goes in bubbles:
        UMRContent.installAll(); // your existing bubble
        GeoDebugContent.install(engine);
        MeshDebugContent.install(engine);
        SkinnedStripDebugContent.install(engine);
        FbxDebugContent.install(engine);
        //System.out.println("[UMR SKIN TEST] install called");
    }
}