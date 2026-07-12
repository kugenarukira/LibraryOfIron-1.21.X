package net.ironedge.libraryofiron.render.physics.surface;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.verlet.DistanceConstraint;
import org.joml.Vector3f;

public final class SurfacePhysicsBuilder {

    private SurfacePhysicsBuilder() {}

    public static PhysicsSimulation buildSurface(
            SurfaceTopology topo,
            float rowSpacing,
            float colSpacing,
            Vector3f origin
    ) {
        return buildSurface(
                topo,
                rowSpacing,
                colSpacing,
                origin,
                SurfaceConstraintConfig.cloth()
        );
    }

    public static PhysicsSimulation buildSurface(
            SurfaceTopology topo,
            float rowSpacing,
            float colSpacing,
            Vector3f origin,
            SurfaceConstraintConfig config
    ) {
        PhysicsSimulation sim = new PhysicsSimulation();

        // Layout:
        // rows move along +X
        // cols move along +Z
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols(); col++) {
                PhysicsPoint p = new PhysicsPoint();

                float x = origin.x + row * rowSpacing;
                float y = origin.y;
                float z = origin.z + col * colSpacing;

                p.position.set(x, y, z);
                p.previousPosition.set(p.position);

                sim.points().add(p);
            }
        }

        // Horizontal structural (across rows)
        for (int row = 0; row < topo.rows() - 1; row++) {
            for (int col = 0; col < topo.cols(); col++) {
                int a = topo.index(row, col);
                int b = topo.index(row + 1, col);

                DistanceConstraint c = new DistanceConstraint(a, b, rowSpacing);
                c.stiffness = config.structuralStiffness;
                sim.constraints().add(c);
            }
        }

        // Vertical structural (down columns)
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols() - 1; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row, col + 1);

                DistanceConstraint c = new DistanceConstraint(a, b, colSpacing);
                c.stiffness = config.structuralStiffness;
                sim.constraints().add(c);
            }
        }

        float diag = (float) Math.sqrt(rowSpacing * rowSpacing + colSpacing * colSpacing);

        // Shear diagonals
        for (int row = 0; row < topo.rows() - 1; row++) {
            for (int col = 0; col < topo.cols() - 1; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row + 1, col + 1);

                DistanceConstraint d1 = new DistanceConstraint(a, b, diag);
                d1.stiffness = config.shearStiffness;
                sim.constraints().add(d1);

                int c = topo.index(row + 1, col);
                int d = topo.index(row, col + 1);

                DistanceConstraint d2 = new DistanceConstraint(c, d, diag);
                d2.stiffness = config.shearStiffness;
                sim.constraints().add(d2);
            }
        }

        // Horizontal bend (skip one row)
        for (int row = 0; row < topo.rows() - 2; row++) {
            for (int col = 0; col < topo.cols(); col++) {
                int a = topo.index(row, col);
                int b = topo.index(row + 2, col);

                DistanceConstraint bend = new DistanceConstraint(a, b, rowSpacing * 2.0f);
                bend.stiffness = config.bendStiffness;
                sim.constraints().add(bend);
            }
        }

        // Vertical bend (skip one column)
        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols() - 2; col++) {
                int a = topo.index(row, col);
                int b = topo.index(row, col + 2);

                DistanceConstraint bend = new DistanceConstraint(a, b, colSpacing * 2.0f);
                bend.stiffness = config.bendStiffness;
                sim.constraints().add(bend);
            }
        }

        sim.gravity.set(config.gravity);
        sim.iterations = config.iterations;
        sim.globalDamping = config.globalDamping;
        sim.selfCollisionEnabled = config.selfCollisionEnabled;
        sim.resetOnPerspectiveChange = config.resetOnPerspectiveChange;

        return sim;
    }
}