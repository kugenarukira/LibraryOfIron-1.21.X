package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.core.FrameContext;
import org.joml.Vector3f;

public final class PhysicsSimulation {
    private String id;
    private final java.util.List<PhysicsPoint> points = new java.util.ArrayList<>();
    private final java.util.List<PhysicsConstraint> constraints = new java.util.ArrayList<>();

    private net.ironedge.libraryofiron.render.core.FrameContext frame;
    public org.joml.Vector3f gravity = new org.joml.Vector3f(0f, -0.8f, 0f);
    public int iterations = 8;
    public boolean enabled = true;
    public boolean selfCollisionEnabled = true;
    public int selfCollisionNeighborSkip = 2;
    public boolean resetOnPerspectiveChange = false;
    public float globalDamping = 0.98f;
    private final java.util.List<PhysicsForce> forces = new java.util.ArrayList<>();
    public java.util.List<PhysicsForce> forces() { return forces; }
    private PhysicsResetHandler resetHandler;

    public String id() { return id; }
    public PhysicsSimulation id(String id) {
        this.id = id;
        return this;
    }

    public PhysicsSimulation resetHandler(PhysicsResetHandler resetHandler) {
        this.resetHandler = resetHandler;
        return this;
    }

    public java.util.List<PhysicsPoint> points() { return points; }
    public java.util.List<PhysicsConstraint> constraints() { return constraints; }
    public net.ironedge.libraryofiron.render.core.FrameContext frame() { return frame; }
    public int startupDelayFrames = 2;
    public int selfCollisionDelayFrames = 20; // ~1s at 20 TPS-ish / render frames depending your step
    private int ageFrames = 0;

    public void step(FrameContext frame, float dt) {
        if (!enabled) return;
        this.frame = frame;
        ageFrames++;

        Boolean changed = frame.attachment("perspectiveChanged", Boolean.class);
        if (resetOnPerspectiveChange && Boolean.TRUE.equals(changed) && resetHandler != null) {
            resetHandler.reset(this, frame);
            ageFrames = 0;
            return;
        }

        if (ageFrames <= startupDelayFrames) {
            if (resetHandler != null) {
                resetHandler.reset(this, frame);
            }
            return;
        }

        for (PhysicsForce force : forces) {
            force.apply(this, frame, dt);
        }

        integrate(dt);

        for (int i = 0; i < iterations; i++) {
            for (PhysicsConstraint c : constraints) {
                c.solve(this, dt);
            }

            net.ironedge.libraryofiron.render.physics.collision.BlockCollisionSolver.solve(this, frame);

            if (selfCollisionEnabled && ageFrames > selfCollisionDelayFrames) {
                net.ironedge.libraryofiron.render.physics.collision.SelfCollisionSolver.solve(this);
            }
        }
    }

    private void integrate(float dt) {
        float dt2 = dt * dt;

        for (PhysicsPoint p : points) {
            if (p.pinned) continue;

            Vector3f velocity = new Vector3f(p.position)
                    .sub(p.previousPosition)
                    .mul(p.damping * globalDamping);

            float maxSpeed = 0.40f;
            float lenSq = velocity.lengthSquared();
            if (lenSq > maxSpeed * maxSpeed) {
                velocity.normalize().mul(maxSpeed);
            }

            Vector3f next = new Vector3f(p.position)
                    .add(velocity)
                    .add(new Vector3f(gravity).add(p.accumulatedForce).mul(dt2));

            if (!Float.isFinite(next.x) || !Float.isFinite(next.y) || !Float.isFinite(next.z)) {
                continue;
            }

            if (p.pinWeight > 0f) {
                Vector3f target = p.pinTarget;
                p.position.lerp(target, p.pinWeight);
            }

            p.previousPosition.set(p.position);
            p.position.set(next);
            p.accumulatedForce.zero();
        }
    }


}