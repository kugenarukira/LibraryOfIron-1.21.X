package net.ironedge.libraryofiron.render.physics.verlet;

import net.ironedge.libraryofiron.render.physics.PhysicsConstraint;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Vector3f;

public final class DistanceConstraint implements PhysicsConstraint {
    public final int a;
    public final int b;
    public float restLength;
    public float stiffness = 1.0f;

    public DistanceConstraint(int a, int b, float restLength) {
        this.a = a;
        this.b = b;
        this.restLength = restLength;
    }

    @Override
    public void solve(PhysicsSimulation sim, float dt) {
        PhysicsPoint pa = sim.points().get(a);
        PhysicsPoint pb = sim.points().get(b);

        Vector3f delta = new Vector3f(pb.position).sub(pa.position);
        float lenSq = delta.lengthSquared();
        if (lenSq < 1.0e-12f) return;

        float len = (float) Math.sqrt(lenSq);

        float wA = pa.pinned ? 0f : pa.inverseMass;
        float wB = pb.pinned ? 0f : pb.inverseMass;
        float wSum = wA + wB;
        if (wSum <= 1.0e-12f) return;

        // positive when stretched, negative when compressed
        float diff = (len - restLength) / len;

        Vector3f correction = new Vector3f(delta).mul(stiffness * diff);

        // move them TOWARD each other when stretched
        if (!pa.pinned) {
            pa.position.add(new Vector3f(correction).mul( wA / wSum));
        }
        if (!pb.pinned) {
            pb.position.add(new Vector3f(correction).mul(-wB / wSum));
        }
    }
}