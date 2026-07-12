package net.ironedge.libraryofiron.render.physics.verlet;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Vector3f;

public final class ChainPhysicsBuilder {
    private ChainPhysicsBuilder() {}

    public static PhysicsSimulation buildLinearChain(int pointCount, float spacing, Vector3f start) {
        PhysicsSimulation sim = new PhysicsSimulation();

        for (int i = 0; i < pointCount; i++) {
            PhysicsPoint p = new PhysicsPoint();
            p.position.set(start.x, start.y - spacing * i, start.z);
            p.previousPosition.set(p.position);
            sim.points().add(p);

            if (i > 0) {
                sim.constraints().add(new DistanceConstraint(i - 1, i, spacing));
            }
        }

        return sim;
    }
}