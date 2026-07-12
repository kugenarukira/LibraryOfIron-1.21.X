package net.ironedge.libraryofiron.render.physics.debug;


import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.provider.HeldItemPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.verlet.ChainResetters;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.physics.verlet.VariableChainPhysicsBuilder;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AmatsuPhysicsContent {

    private static boolean installed = false;

    private AmatsuPhysicsContent() {}

    public static void install() {
        if (installed) return;
        installed = true;

        float baseLen = 7.825f / 16.0f;
        float midLen  = 7.625f / 16.0f;
        float tipLen  = 9.325f / 16.0f;

        // 1 base + 7 mid + 1 tip = 9 segments
        float[] lengths = new float[] {
                baseLen,
                midLen, midLen, midLen, midLen, midLen, midLen, midLen,
                tipLen
        };

        PhysicsSimulation sim = VariableChainPhysicsBuilder.buildChain(
                lengths,
                new Vector3f(0f, 0f, 0f)
        ).id("amatsuquiz_chain");

// Prevent POV swap resets
        sim.resetOnPerspectiveChange = false;

        sim.gravity.set(0f, -0.35f, 0f);
        sim.iterations = 10;
        sim.selfCollisionEnabled = true;
        sim.selfCollisionNeighborSkip = 2;

        sim.gravity.set(0f, -0.35f, 0f);
        sim.iterations = 10;
        sim.selfCollisionEnabled = true;
        sim.selfCollisionNeighborSkip = 2;

        HeldItemPhysicsTarget rootTarget = new HeldItemPhysicsTarget(
                "player",
                PlayerAnchorMap.INSTANCE,
                new Vector3f( 0.08f, -0.10f,  0.12f), // front / whip side
                new Vector3f(-0.08f, -0.10f, -0.12f)  // back / dagger side
        );

        PinConstraint pin = new PinConstraint(0, rootTarget);
        pin.teleportResetDistance = 0.20f;
        pin.inheritTargetMotion = 1.0f;
        pin.neighborMotionInfluence = 0.45f;
        pin.rotationalInfluencePoints = 4;
        pin.rotationalInfluenceFalloff = 0.70f;

        sim.startupDelayFrames = 1;
        sim.selfCollisionDelayFrames = 8;
        sim.constraints().add(pin);

        PhysicsPoint root = sim.points().get(0);
        root.pinned = true;

        sim.resetHandler((s, frame) -> {
            Vector3f rootPos = rootTarget.samplePosition(frame);
            if (rootPos == null) return;

            Quaternionf rootRot = rootTarget.sampleRotation(frame);

            // use the actual first segment length as spacing seed
            ChainResetters.resetFromRoot(s, rootPos, rootRot, baseLen);
        });

        PhysicsSystem.get().add(sim);

        AmatsuPhysicsInstaller.install("amatsuquiz_chain", lengths.length + 1);
    }
}