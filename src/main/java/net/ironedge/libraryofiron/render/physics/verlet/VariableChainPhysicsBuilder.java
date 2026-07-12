package net.ironedge.libraryofiron.render.physics.verlet;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Vector3f;

public final class VariableChainPhysicsBuilder {
    private VariableChainPhysicsBuilder() {}

    /**
     * segmentLengths.length = number of segments
     * resulting point count = segmentLengths.length + 1
     */
    public static PhysicsSimulation buildChain(float[] segmentLengths, Vector3f start) {
        PhysicsSimulation sim = new PhysicsSimulation();

        PhysicsPoint root = new PhysicsPoint();
        root.position.set(start);
        root.previousPosition.set(start);
        sim.points().add(root);

        float y = start.y;

        for (int i = 0; i < segmentLengths.length; i++) {
            float len = segmentLengths[i];
            y -= len;

            PhysicsPoint p = new PhysicsPoint();
            p.position.set(start.x, y, start.z);
            p.previousPosition.set(p.position);
            sim.points().add(p);

            sim.constraints().add(new DistanceConstraint(i, i + 1, len));
        }

        return sim;
    }
}