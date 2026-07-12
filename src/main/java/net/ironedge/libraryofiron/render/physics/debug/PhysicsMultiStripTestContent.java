package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.strip.StripPhysicsBuilder;
import net.ironedge.libraryofiron.render.physics.strip.StripTopology;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import org.joml.Vector3f;

public final class PhysicsMultiStripTestContent {

    private PhysicsMultiStripTestContent() {}

    private static boolean installed = false;

    public static final StripTopology TOPO = new StripTopology(2, 8);

    public static final String LEFT_ID   = "test_strip_left";
    public static final String CENTER_ID = "test_strip_center";
    public static final String RIGHT_ID  = "test_strip_right";

    public static void install() {
        if (installed) return;
        installed = true;

        installShoulderStrip(
                LEFT_ID,
                AnchorKeys.SHOULDER_L,
                new Vector3f(-0.18f, -0.05f, 0.08f)
        );

        installTorsoStrip(
                CENTER_ID,
                AnchorKeys.TORSO,
                new Vector3f(-0.125f, -0.10f, 0.10f)
        );

        installShoulderStrip(
                RIGHT_ID,
                AnchorKeys.SHOULDER_R,
                new Vector3f(-0.07f, -0.05f, 0.08f)
        );
    }

    private static void installShoulderStrip(String simId, net.ironedge.libraryofiron.render.anchor.AnchorKey anchor, Vector3f offset) {
        PhysicsSimulation sim = StripPhysicsBuilder.buildStrip(
                TOPO,
                0.20f,
                0.18f,
                new Vector3f(0f, 0f, 0f)
        ).id(simId);

        sim.gravity.set(0f, -0.22f, 0f);
        sim.iterations = 10;
        sim.globalDamping = 0.985f;
        sim.selfCollisionEnabled = true;
        sim.resetOnPerspectiveChange = false;

        sim.startupDelayFrames = 1;
        sim.selfCollisionDelayFrames = 8;

        PoseGraphPhysicsTarget leftTarget = new PoseGraphPhysicsTarget(
                "player",
                anchor,
                PlayerAnchorMap.INSTANCE
        ) {
            @Override
            public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                Vector3f base = super.samplePosition(frame);
                if (base == null) return null;

                org.joml.Quaternionf rot = sampleRotation(frame);

                Vector3f local = new Vector3f(offset);
                local.rotate(rot);

                return base.add(local);
            }
        };

        PoseGraphPhysicsTarget rightTarget = new PoseGraphPhysicsTarget(
                "player",
                anchor,
                PlayerAnchorMap.INSTANCE
        ) {
            @Override
            public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                Vector3f base = super.samplePosition(frame);
                if (base == null) return null;

                org.joml.Quaternionf rot = sampleRotation(frame);

                Vector3f local = new Vector3f(offset).add(0.18f, 0f, 0f);
                local.rotate(rot);

                return base.add(local);
            }
        };

        PinConstraint pinA = new PinConstraint(TOPO.index(0, 0), leftTarget);
        pinA.inheritTargetMotion = 0.0f;

        PinConstraint pinB = new PinConstraint(TOPO.index(1, 0), rightTarget);
        pinB.inheritTargetMotion = 0.0f;


        pinA.neighborMotionInfluence = 0.0f;
        pinB.neighborMotionInfluence = 0.0f;
        pinA.rotationalInfluencePoints = 0;
        pinB.rotationalInfluencePoints = 0;

        sim.constraints().add(pinA);
        sim.constraints().add(pinB);

        sim.points().get(TOPO.index(0, 0)).pinned = true;
        sim.points().get(TOPO.index(1, 0)).pinned = true;

        PhysicsSystem.get().add(sim);
    }

    private static void installTorsoStrip(String simId, net.ironedge.libraryofiron.render.anchor.AnchorKey anchor, Vector3f offset) {
        PhysicsSimulation sim = StripPhysicsBuilder.buildStrip(
                TOPO,
                0.20f,
                0.25f,
                new Vector3f(0f, 0f, 0f)
        ).id(simId);

        sim.gravity.set(0f, -0.22f, 0f);
        sim.iterations = 10;
        sim.globalDamping = 0.985f;
        sim.selfCollisionEnabled = true;
        sim.resetOnPerspectiveChange = false;

        PoseGraphPhysicsTarget leftTarget = new PoseGraphPhysicsTarget(
                "player",
                anchor,
                PlayerAnchorMap.INSTANCE
        ) {
            @Override
            public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                Vector3f base = super.samplePosition(frame);
                if (base == null) return null;

                org.joml.Quaternionf rot = sampleRotation(frame);

                Vector3f local = new Vector3f(offset);
                local.rotate(rot);

                return base.add(local);
            }
        };

        PoseGraphPhysicsTarget rightTarget = new PoseGraphPhysicsTarget(
                "player",
                anchor,
                PlayerAnchorMap.INSTANCE
        ) {
            @Override
            public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                Vector3f base = super.samplePosition(frame);
                if (base == null) return null;

                org.joml.Quaternionf rot = sampleRotation(frame);

                Vector3f local = new Vector3f(offset).add(0.25f, 0f, 0f);
                local.rotate(rot);

                return base.add(local);
            }
        };

        PinConstraint pinA = new PinConstraint(TOPO.index(0, 0), leftTarget);
        pinA.inheritTargetMotion = 0.0f;

        PinConstraint pinB = new PinConstraint(TOPO.index(1, 0), rightTarget);
        pinB.inheritTargetMotion = 0.0f;

        sim.constraints().add(pinA);
        sim.constraints().add(pinB);

        sim.points().get(TOPO.index(0, 0)).pinned = true;
        sim.points().get(TOPO.index(1, 0)).pinned = true;

        PhysicsSystem.get().add(sim);
    }
}