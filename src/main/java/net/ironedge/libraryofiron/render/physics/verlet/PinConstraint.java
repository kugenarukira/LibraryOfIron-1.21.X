package net.ironedge.libraryofiron.render.physics.verlet;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsConstraint;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsTarget;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PinConstraint implements PhysicsConstraint {
    public final int pointIndex;
    public final PhysicsTarget target;

    // big translation jump = reset fake verlet velocity
    public float teleportResetDistance = 0.20f;

    // preserve target translation motion in pinned point
    public float inheritTargetMotion = 1.0f;

    // push some target translation into nearby free points
    public float neighborMotionInfluence = 0.35f;

    // rotate first few free points with the root
    public int rotationalInfluencePoints = 3;
    public float rotationalInfluenceFalloff = 0.65f;

    private final Vector3f lastTargetPos = new Vector3f();
    private final Quaternionf lastTargetRot = new Quaternionf();
    public float pinWeight;
    private boolean hasLastTarget = false;

    public PinConstraint(int pointIndex, PhysicsTarget target) {
        this.pointIndex = pointIndex;
        this.target = target;
    }

    @Override
    public void solve(PhysicsSimulation sim, float dt) {
        FrameContext frame = sim.frame();
        if (frame == null) return;
        if (pointIndex < 0 || pointIndex >= sim.points().size()) return;

        Vector3f targetPos = target.samplePosition(frame);
        if (targetPos == null) return;

        Quaternionf targetRot = target.sampleRotation(frame);
        if (targetRot == null) targetRot = new Quaternionf();

        PhysicsPoint root = sim.points().get(pointIndex);

        Vector3f targetDelta = new Vector3f();
        Quaternionf deltaRot = new Quaternionf();

        if (hasLastTarget) {
            targetDelta.set(targetPos).sub(lastTargetPos);

            Quaternionf invLast = new Quaternionf(lastTargetRot).invert();
            deltaRot.set(targetRot).mul(invLast);
        }

        float jumpSq = targetDelta.lengthSquared();

        // pin root exactly
        root.position.set(targetPos);

        if (!hasLastTarget || jumpSq > teleportResetDistance * teleportResetDistance) {
            root.previousPosition.set(targetPos);
        } else {
            Vector3f inherited = new Vector3f(targetDelta).mul(inheritTargetMotion);
            root.previousPosition.set(new Vector3f(targetPos).sub(inherited));
        }

        // translation inheritance
        if (hasLastTarget && neighborMotionInfluence > 0f) {
            for (int i = 1; i <= rotationalInfluencePoints; i++) {
                int idx = pointIndex + i;
                if (idx >= sim.points().size()) break;

                PhysicsPoint p = sim.points().get(idx);
                if (p.pinned) continue;

                float w = (float) Math.pow(rotationalInfluenceFalloff, i - 1);
                Vector3f injected = new Vector3f(targetDelta).mul(neighborMotionInfluence * w);
                p.position.add(injected);
                p.previousPosition.add(injected);
            }
        }

        // rotational inheritance
        if (hasLastTarget) {
            for (int i = 1; i <= rotationalInfluencePoints; i++) {
                int idx = pointIndex + i;
                if (idx >= sim.points().size()) break;

                PhysicsPoint p = sim.points().get(idx);
                if (p.pinned) continue;

                float w = (float) Math.pow(rotationalInfluenceFalloff, i - 1);
                Quaternionf partialRot = new Quaternionf().slerp(new Quaternionf(deltaRot), w);

                Vector3f posOffset = new Vector3f(p.position).sub(targetPos);
                Vector3f prevOffset = new Vector3f(p.previousPosition).sub(targetPos);

                posOffset.rotate(partialRot);
                prevOffset.rotate(partialRot);

                p.position.set(new Vector3f(targetPos).add(posOffset));
                p.previousPosition.set(new Vector3f(targetPos).add(prevOffset));
            }
        }

        lastTargetPos.set(targetPos);
        lastTargetRot.set(targetRot);
        hasLastTarget = true;
    }
}