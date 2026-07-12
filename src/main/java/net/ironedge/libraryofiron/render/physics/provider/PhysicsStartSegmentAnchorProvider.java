package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorProvider;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorTransform;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PhysicsStartSegmentAnchorProvider implements DynamicAnchorProvider {

    private static final Vector3f LOCAL_FORWARD = new Vector3f(0f, 0f, 1f);

    private final String simulationId;
    private final int a;
    private final int b;

    public PhysicsStartSegmentAnchorProvider(String simulationId, int a, int b) {
        this.simulationId = simulationId;
        this.a = a;
        this.b = b;
    }

    @Override
    public AnchorTransform sample(AnchorKey key, AnchorResolutionContext ctx) {
        PhysicsSimulation sim = PhysicsSystem.get().getById(simulationId);
        if (sim == null) return AnchorTransform.identity();
        if (a < 0 || b < 0 || a >= sim.points().size() || b >= sim.points().size()) {
            return AnchorTransform.identity();
        }

        PhysicsPoint pa = sim.points().get(a);
        PhysicsPoint pb = sim.points().get(b);

        Vector3f start = new Vector3f(pa.position);
        Vector3f forward = new Vector3f(pb.position).sub(pa.position);

        if (forward.lengthSquared() < 1.0e-8f) {
            return new AnchorTransform(start, new Quaternionf(), new Vector3f(1, 1, 1));
        }

        forward.normalize();

        Quaternionf rot = new Quaternionf().rotationTo(
                new Vector3f(LOCAL_FORWARD),
                forward
        );

        return new AnchorTransform(start, rot, new Vector3f(1, 1, 1));
    }
}