package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorProvider;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorTransform;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PhysicsPointAnchorProvider implements DynamicAnchorProvider {

    private static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

    private final String simulationId;
    private final int pointIndex;

    public PhysicsPointAnchorProvider(String simulationId, int pointIndex) {
        this.simulationId = simulationId;
        this.pointIndex = pointIndex;
    }

    @Override
    public AnchorTransform sample(AnchorKey key, AnchorResolutionContext ctx) {
        PhysicsSimulation sim = PhysicsSystem.get().getById(simulationId);
        if (sim == null) return AnchorTransform.identity();
        if (pointIndex < 0 || pointIndex >= sim.points().size()) return AnchorTransform.identity();

        PhysicsPoint p = sim.points().get(pointIndex);
        Vector3f pos = new Vector3f(p.position);

        Vector3f forward = sampleForward(sim, pointIndex);
        if (forward == null || forward.lengthSquared() < 1.0e-8f) {
            return new AnchorTransform(pos, new Quaternionf(), new Vector3f(1, 1, 1));
        }
        forward.normalize();

        Vector3f up = new Vector3f(WORLD_UP);

        // If forward is too parallel to up, pick another fallback up
        if (Math.abs(forward.dot(up)) > 0.999f) {
            up.set(1, 0, 0);
        }

        Vector3f right = new Vector3f(up).cross(forward);
        if (right.lengthSquared() < 1.0e-8f) {
            return new AnchorTransform(pos, new Quaternionf(), new Vector3f(1, 1, 1));
        }
        right.normalize();

        up = new Vector3f(forward).cross(right).normalize();

        // Columns are basis vectors: right, up, forward
        Matrix3f basis = new Matrix3f(
                right.x, up.x, forward.x,
                right.y, up.y, forward.y,
                right.z, up.z, forward.z
        );

        Quaternionf rot = new Quaternionf().setFromNormalized(basis);

        return new AnchorTransform(pos, rot, new Vector3f(1, 1, 1));
    }

    private static Vector3f sampleForward(PhysicsSimulation sim, int i) {
        if (i + 1 < sim.points().size()) {
            return new Vector3f(sim.points().get(i + 1).position)
                    .sub(sim.points().get(i).position);
        }
        if (i - 1 >= 0) {
            return new Vector3f(sim.points().get(i).position)
                    .sub(sim.points().get(i - 1).position);
        }
        return null;
    }
}