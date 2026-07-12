package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.forces.DragForce;
import net.ironedge.libraryofiron.render.physics.forces.WindForce;
import net.ironedge.libraryofiron.render.physics.forces.WindPreset;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceConstraintConfig;
import net.ironedge.libraryofiron.render.physics.surface.SurfacePhysicsBuilder;
import net.ironedge.libraryofiron.render.physics.surface.SurfacePinPresets;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import org.joml.Vector3f;

public final class PhysicsMembraneSurfaceTestContent {

    private static boolean installed = false;

    public static final String SIM_ID = "membrane_surface_test";
    public static final SurfaceTopology TOPO = new SurfaceTopology(12, 12);

    private PhysicsMembraneSurfaceTestContent() {}

    public static void install() {
        if (installed) return;
        installed = true;

        float rowSpacing = 0.16f;
        float colSpacing = 0.18f;

        SurfaceConstraintConfig config = SurfaceConstraintConfig.membrane();

        PhysicsSimulation sim = SurfacePhysicsBuilder.buildSurface(
                TOPO,
                rowSpacing,
                colSpacing,
                new Vector3f(0f, 0f, 0f),
                config
        ).id(SIM_ID);


        SurfacePinPresets.pinLeftEdgeToAnchor(
                sim,
                TOPO,
                "player",
                AnchorKeys.HAND_L,
                PlayerAnchorMap.INSTANCE,
                new Vector3f(0f, 0f, 0f),
                colSpacing
        );

        SurfacePinPresets.pinRightEdgeToAnchor(
                sim,
                TOPO,
                "player",
                AnchorKeys.HAND_R,
                PlayerAnchorMap.INSTANCE,
                new Vector3f(0f, 0f, 0f),
                colSpacing
        );

        sim.forces().add(new WindForce(
                TOPO,
                WindPreset.breeze()
        ));

        sim.forces().add(new DragForce(0.12f));

        sim.startupDelayFrames = 1;
        sim.selfCollisionDelayFrames = 8;

        PhysicsSystem.get().add(sim);
    }
}