package net.ironedge.libraryofiron.render.physics.collision;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Vector3f;

public final class SelfCollisionSolver {

    private SelfCollisionSolver() {}

    public static void solve(PhysicsSimulation sim) {
        if (!sim.selfCollisionEnabled) return;

        int skip = Math.max(0, sim.selfCollisionNeighborSkip);
        int n = sim.points().size();

        for (int i = 0; i < n; i++) {
            PhysicsPoint a = sim.points().get(i);

            for (int j = i + 1; j < n; j++) {
                if (Math.abs(i - j) <= skip) continue;

                PhysicsPoint b = sim.points().get(j);
                solvePair(a, b);
            }
        }
    }

    private static void solvePair(PhysicsPoint a, PhysicsPoint b) {
        float r = a.radius + b.radius;

        Vector3f delta = new Vector3f(b.position).sub(a.position);
        float distSq = delta.lengthSquared();
        float rSq = r * r;

        if (distSq >= rSq) return;

        float wA = a.pinned ? 0f : a.inverseMass;
        float wB = b.pinned ? 0f : b.inverseMass;
        float wSum = wA + wB;
        if (wSum <= 1.0e-12f) return;

        if (distSq > 1.0e-12f) {
            float dist = (float) Math.sqrt(distSq);
            float push = r - dist;

            Vector3f normal = delta.div(dist); // normalize in place

            if (!a.pinned) {
                a.position.add(new Vector3f(normal).mul(-push * (wA / wSum)));
            }
            if (!b.pinned) {
                b.position.add(new Vector3f(normal).mul( push * (wB / wSum)));
            }

            return;
        }

        // Degenerate overlap: same position
        // Choose a stable fallback axis
        Vector3f normal = new Vector3f(1f, 0f, 0f);
        float push = r;

        if (!a.pinned) {
            a.position.add(new Vector3f(normal).mul(-push * (wA / wSum)));
        }
        if (!b.pinned) {
            b.position.add(new Vector3f(normal).mul( push * (wB / wSum)));
        }
    }
}