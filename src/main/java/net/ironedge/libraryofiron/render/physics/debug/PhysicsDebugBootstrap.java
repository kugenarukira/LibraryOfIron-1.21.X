package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.physics.segmentedsurface.RibbonStripMaterial;
import net.ironedge.libraryofiron.render.physics.segmentedsurface.RibbonStripRenderNode;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDebugInstances;

public final class PhysicsDebugBootstrap {

    private PhysicsDebugBootstrap() {}

    public static void install(RenderEngine engine) {
        PhysicsDebugContent.install();
        PhysicsStripDebugContent.install();

        boolean DEBUG_CHAIN = false;
        boolean DEBUG_COLLISION = false;
        boolean DEBUG_STRIP = false;
        boolean DEBUG_WHIP = false;
        boolean DEBUG_SURFACE = true;
        boolean DEBUG_MEMBRANE = false;

        if (DEBUG_CHAIN) {
            engine.graph().addNode(new PhysicsDebugDrawNode());
            engine.graph().addNode(new PhysicsCurveDebugNode("debug_chain", 0xFF00FFFF));
        }

        if (DEBUG_COLLISION) {
            engine.graph().addNode(new PhysicsCollisionDebugNode("debug_chain", 0xFFFFFF00));
            engine.graph().addNode(new PhysicsCollisionContactDebugNode());
        }

        /*if (DEBUG_STRIP) {
            PhysicsMultiStripTestContent.install();

            engine.graph().addNode(new RibbonStripRenderNode(
                    PhysicsMultiStripTestContent.LEFT_ID,
                    PhysicsMultiStripTestContent.TOPO,
                    new RibbonStripMaterial(UMaterialDebugInstances.BASIC_INSTANCE)
            ));

            engine.graph().addNode(new RibbonStripRenderNode(
                    PhysicsMultiStripTestContent.CENTER_ID,
                    PhysicsMultiStripTestContent.TOPO,
                    new RibbonStripMaterial(UMaterialDebugInstances.TINTED_BLUE)
            ));

            engine.graph().addNode(new RibbonStripRenderNode(
                    PhysicsMultiStripTestContent.RIGHT_ID,
                    PhysicsMultiStripTestContent.TOPO,
                    new RibbonStripMaterial(UMaterialDebugInstances.EMISSIVE_HOT)
            ));
        }

        if (DEBUG_WHIP) {
            engine.graph().addNode(new PhysicsDebugDrawNode());
        }

        if (DEBUG_SURFACE) {
            PhysicsCapeSurfaceTestContent.install();

            engine.graph().addNode(new PhysicsSurfaceDebugNode(
                    PhysicsCapeSurfaceTestContent.SIM_ID,
                    PhysicsCapeSurfaceTestContent.TOPO
            ));

            engine.graph().addNode(new net.ironedge.libraryofiron.render.segmented.SurfaceRenderNode(
                    PhysicsCapeSurfaceTestContent.SIM_ID,
                    PhysicsCapeSurfaceTestContent.TOPO,
                    new net.ironedge.libraryofiron.render.segmented.SurfaceMaterial(
                            UMaterialDebugInstances.EMISSIVE_HOT
                    )
            ));
        }*/

        if (DEBUG_MEMBRANE) {
            PhysicsMembraneSurfaceTestContent.install();

            engine.graph().addNode(new PhysicsSurfaceDebugNode(
                    PhysicsMembraneSurfaceTestContent.SIM_ID,
                    PhysicsMembraneSurfaceTestContent.TOPO
            ));

            engine.graph().addNode(new net.ironedge.libraryofiron.render.segmented.SurfaceRenderNode(
                    PhysicsMembraneSurfaceTestContent.SIM_ID,
                    PhysicsMembraneSurfaceTestContent.TOPO,
                    new net.ironedge.libraryofiron.render.segmented.SurfaceMaterial(
                            UMaterialDebugInstances.EMISSIVE_HOT
                    )
            ));
        }
    }
}