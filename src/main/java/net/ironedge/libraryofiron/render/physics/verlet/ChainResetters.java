package net.ironedge.libraryofiron.render.physics.verlet;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ChainResetters {

    private ChainResetters() {}

    public static void resetFromRoot(
            PhysicsSimulation sim,
            Vector3f rootPos,
            Quaternionf rootRot,
            float spacing
    ) {
        if (sim.points().isEmpty()) return;

        // local down in root space
        Vector3f dir = new Vector3f(0f, -1f, 0f).rotate(rootRot).normalize();

        for (int i = 0; i < sim.points().size(); i++) {
            PhysicsPoint p = sim.points().get(i);
            Vector3f pos = new Vector3f(rootPos).add(new Vector3f(dir).mul(spacing * i));
            p.position.set(pos);
            p.previousPosition.set(pos);
            p.accumulatedForce.zero();
        }
    }

    public static float estimateSpacing(PhysicsSimulation sim, float fallback) {
        for (var c : sim.constraints()) {
            if (c instanceof DistanceConstraint d) {
                return d.restLength;
            }
        }
        return fallback;
    }
}