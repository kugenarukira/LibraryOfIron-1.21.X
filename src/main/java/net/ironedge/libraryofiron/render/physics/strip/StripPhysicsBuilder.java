package net.ironedge.libraryofiron.render.physics.strip;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.verlet.DistanceConstraint;
import org.joml.Vector3f;

public final class StripPhysicsBuilder {

    private StripPhysicsBuilder() {}

    public static PhysicsSimulation buildTwoRowStrip(
            int columns,
            float segmentSpacing,
            float stripWidth,
            Vector3f origin
    ) {
        return buildStrip(
                new StripTopology(2, columns),
                segmentSpacing,
                stripWidth,
                origin
        );
    }

    public static PhysicsSimulation buildStrip(
            StripTopology topo,
            float segmentSpacing,
            float stripWidth,
            Vector3f origin
    ) {
        PhysicsSimulation sim = new PhysicsSimulation();

        // Points
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols(); col++) {
                PhysicsPoint p = new PhysicsPoint();

                float x = origin.x + row * stripWidth;
                float y = origin.y;
                float z = origin.z + col * segmentSpacing;

                p.position.set(x, y, z);
                p.previousPosition.set(p.position);
                sim.points().add(p);
            }
        }

        // Length constraints
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols() - 1; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row, col + 1);

                DistanceConstraint c = new DistanceConstraint(a, b, segmentSpacing);
                c.stiffness = 1.0f;
                sim.constraints().add(c);
            }
        }

        // Width constraints
        for (int row = 0; row < topo.rows() - 1; row++) {
            for (int col = 0; col < topo.cols(); col++) {
                int a = topo.index(row, col);
                int b = topo.index(row + 1, col);

                DistanceConstraint c = new DistanceConstraint(a, b, stripWidth);
                c.stiffness = 1.0f;
                sim.constraints().add(c);
            }
        }

        // Shear diagonals
        float diag = (float) Math.sqrt(segmentSpacing * segmentSpacing + stripWidth * stripWidth);

        for (int row = 0; row < topo.rows() - 1; row++) {
            for (int col = 0; col < topo.cols() - 1; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row + 1, col + 1);
                DistanceConstraint d1 = new DistanceConstraint(a, b, diag);
                d1.stiffness = 0.9f;
                sim.constraints().add(d1);

                int c = topo.index(row + 1, col);
                int d = topo.index(row, col + 1);
                DistanceConstraint d2 = new DistanceConstraint(c, d, diag);
                d2.stiffness = 0.9f;
                sim.constraints().add(d2);
            }
        }

        // Bend resistance: skip-one constraints along each row
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols() - 2; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row, col + 2);

                DistanceConstraint bend = new DistanceConstraint(a, b, segmentSpacing * 2.0f);
                bend.stiffness = 0.35f; // softer than structural constraints
                sim.constraints().add(bend);
            }
        }

        return sim;
    }
}