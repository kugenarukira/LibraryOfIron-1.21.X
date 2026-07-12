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

public final class PhysicsSegmentAnchorProvider implements DynamicAnchorProvider {

    private static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

    private final String simulationId;
    private final int aIndex;
    private final int bIndex;
    private final boolean scaleAlongZ;

    public PhysicsSegmentAnchorProvider(String simulationId, int aIndex, int bIndex) {
        this(simulationId, aIndex, bIndex, false);
    }

    public PhysicsSegmentAnchorProvider(String simulationId, int aIndex, int bIndex, boolean scaleAlongZ) {
        this.simulationId = simulationId;
        this.aIndex = aIndex;
        this.bIndex = bIndex;
        this.scaleAlongZ = scaleAlongZ;
    }

    @Override
    public AnchorTransform sample(AnchorKey key, AnchorResolutionContext ctx) {
        PhysicsSimulation sim = PhysicsSystem.get().getById(simulationId);
        if (sim == null) return AnchorTransform.identity();
        if (aIndex < 0 || bIndex < 0) return AnchorTransform.identity();
        if (aIndex >= sim.points().size() || bIndex >= sim.points().size()) return AnchorTransform.identity();

        PhysicsPoint a = sim.points().get(aIndex);
        PhysicsPoint b = sim.points().get(bIndex);

        Vector3f pa = new Vector3f(a.position);
        Vector3f pb = new Vector3f(b.position);

        Vector3f forward = new Vector3f(pb).sub(pa);
        float lenSq = forward.lengthSquared();
        if (lenSq < 1.0e-8f) {
            return new AnchorTransform(
                    new Vector3f(pa),
                    new Quaternionf(),
                    new Vector3f(1, 1, 1)
            );
        }

        float len = (float) Math.sqrt(lenSq);
        forward.div(len);

        Vector3f pos = new Vector3f(pa).add(pb).mul(0.5f);

        Vector3f up = new Vector3f(WORLD_UP);
        if (Math.abs(forward.dot(up)) > 0.999f) {
            up.set(1, 0, 0);
        }

        Vector3f right = new Vector3f(up).cross(forward);
        if (right.lengthSquared() < 1.0e-8f) {
            return new AnchorTransform(pos, new Quaternionf(), new Vector3f(1, 1, 1));
        }
        right.normalize();

        up = new Vector3f(forward).cross(right).normalize();

        Matrix3f basis = new Matrix3f(
                right.x, up.x, forward.x,
                right.y, up.y, forward.y,
                right.z, up.z, forward.z
        );

        Quaternionf rot = new Quaternionf().setFromNormalized(basis);

        Vector3f scale = scaleAlongZ
                ? new Vector3f(1f, 1f, len)
                : new Vector3f(1f, 1f, 1f);

        return new AnchorTransform(pos, rot, scale);
    }
}