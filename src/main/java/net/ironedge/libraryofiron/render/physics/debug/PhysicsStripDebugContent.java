package net.ironedge.libraryofiron.render.physics.debug;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.strip.StripPhysicsBuilder;
import net.ironedge.libraryofiron.render.physics.strip.StripTopology;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import net.minecraft.client.Minecraft;
import org.joml.Vector3f;

public final class PhysicsStripDebugContent {

    private static boolean installed = false;
    public static final String SIM_ID = "debug_strip";
    public static final StripTopology TOPO = new StripTopology(2, 8);

    private PhysicsStripDebugContent() {}

    public static void install() {
        if (installed) return;
        installed = true;

        PhysicsSimulation sim = StripPhysicsBuilder.buildStrip(
                TOPO,
                0.20f,                 // spacing along strip
                0.25f,                 // strip width
                new Vector3f(0f, 0f, 0f)
        ).id(SIM_ID);

        sim.gravity.set(0f, -0.25f, 0f);
        sim.globalDamping = 0.985f;
        sim.iterations = 10;
        sim.selfCollisionEnabled = true;
        sim.resetOnPerspectiveChange = false;

        // pin top-left to left shoulder
        PoseGraphPhysicsTarget leftTarget = new PoseGraphPhysicsTarget(
                "player",
                AnchorKeys.HAND_L,
                PlayerAnchorMap.INSTANCE
        );

        PoseGraphPhysicsTarget rightTarget = new PoseGraphPhysicsTarget(
                "player",
                AnchorKeys.HAND_R,
                PlayerAnchorMap.INSTANCE
        );

        PinConstraint leftPin = new PinConstraint(TOPO.index(0, 0), leftTarget);
        leftPin.inheritTargetMotion = 0.0f;

        PinConstraint rightPin = new PinConstraint(TOPO.index(1, 0), rightTarget);
        rightPin.inheritTargetMotion = 0.0f;

        sim.constraints().add(leftPin);
        sim.constraints().add(rightPin);

        sim.points().get(TOPO.index(0, 0)).pinned = true;
        sim.points().get(TOPO.index(1, 0)).pinned = true;

        PhysicsSystem.get().add(sim);
    }
}