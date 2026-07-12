package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.surface.SurfacePhysicsBuilder;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import org.joml.Vector3f;

public final class PhysicsSurfaceDebugContent {

    private static boolean installed = false;

    public static final String SIM_ID = "debug_surface";
    public static final SurfaceTopology TOPO = new SurfaceTopology(4, 8);

    private PhysicsSurfaceDebugContent() {}

    public static void install() {
        if (installed) return;
        installed = true;

        PhysicsSimulation sim = SurfacePhysicsBuilder.buildSurface(
                TOPO,
                0.18f,              // row spacing (width)
                0.20f,              // col spacing (length)
                new Vector3f(0f, 0f, 0f)
        ).id(SIM_ID);

        sim.gravity.set(0f, -0.20f, 0f);
        sim.iterations = 10;
        sim.globalDamping = 0.985f;
        sim.selfCollisionEnabled = true;
        sim.resetOnPerspectiveChange = false;

        sim.startupDelayFrames = 1;
        sim.selfCollisionDelayFrames = 8;

        // Pin entire top row across torso width
        for (int row = 0; row < TOPO.rows(); row++) {
            final float xOffset = row * 0.18f - ((TOPO.rows() - 1) * 0.18f * 0.5f);

            PoseGraphPhysicsTarget target = new PoseGraphPhysicsTarget(
                    "player",
                    AnchorKeys.TORSO,
                    PlayerAnchorMap.INSTANCE
            ) {
                @Override
                public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                    Vector3f base = super.samplePosition(frame);
                    if (base == null) return null;

                    org.joml.Quaternionf rot = sampleRotation(frame);
                    Vector3f local = new Vector3f(xOffset, -0.05f, 0.08f).rotate(rot);
                    return base.add(local);
                }
            };

            int idx = TOPO.index(row, 0);
            PinConstraint pin = new PinConstraint(idx, target);
            pin.inheritTargetMotion = 0.0f;
            pin.neighborMotionInfluence = 0.0f;
            pin.rotationalInfluencePoints = 0;

            sim.constraints().add(pin);
            sim.points().get(idx).pinned = true;
        }

        PhysicsSystem.get().add(sim);
    }
}