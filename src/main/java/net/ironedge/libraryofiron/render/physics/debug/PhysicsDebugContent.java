package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.core.registry.LoIRegistry;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsAnchorInstaller;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsPointAnchorProvider;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.verlet.ChainPhysicsBuilder;
import net.ironedge.libraryofiron.render.physics.verlet.ChainResetters;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PhysicsDebugContent {

    private PhysicsDebugContent() {}

    private static boolean installed = false;

    public static void install() {
        if (installed) return;
        installed = true;

        int pointCount = 12;

        PhysicsSimulation sim = ChainPhysicsBuilder.buildLinearChain(
                pointCount,
                0.20f,
                new Vector3f(0f, 0f, 0f)
        ).id("debug_chain");

        // install generated point + segment anchors for THIS sim
        PhysicsAnchorInstaller.installChain(
                LoIRegistry.dynamicResolver,
                "debug_chain",
                "debug_chain",
                pointCount
        );

        PoseGraphPhysicsTarget rootTarget = new PoseGraphPhysicsTarget(
                "player",
                AnchorKeys.HAND_R,
                PlayerAnchorMap.INSTANCE
        );

        PinConstraint pin = new PinConstraint(0, rootTarget);
        sim.constraints().add(pin);

        sim.startupDelayFrames = 1;
        sim.selfCollisionDelayFrames = 8;

        PhysicsPoint root = sim.points().get(0);
        root.pinned = true;

        sim.resetHandler((s, frame) -> {
            Vector3f rootPos = rootTarget.samplePosition(frame);
            if (rootPos == null) return;

            Quaternionf rootRot = rootTarget.sampleRotation(frame);
            float spacing = ChainResetters.estimateSpacing(s, 0.20f);

            ChainResetters.resetFromRoot(
                    s,
                    rootPos,
                    rootRot,
                    spacing
            );
        });

        // explicit shared "tip" key -> always true last point
        LoIRegistry.dynamicResolver.registerProvider(
                AnchorKeys.PHYSICS_CHAIN_TIP,
                new PhysicsPointAnchorProvider("debug_chain", pointCount - 1)
        );

        PhysicsSystem.get().add(sim);
    }
}