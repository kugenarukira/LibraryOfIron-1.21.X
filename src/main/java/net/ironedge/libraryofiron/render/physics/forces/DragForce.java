package net.ironedge.libraryofiron.render.physics.forces;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsForce;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import org.joml.Vector3f;

public final class DragForce implements PhysicsForce {

    private final float drag;

    public DragForce(float drag) {
        this.drag = drag;
    }

    @Override
    public void apply(PhysicsSimulation sim, FrameContext frame, float dt) {
        for (PhysicsPoint p : sim.points()) {
            Vector3f velocity = new Vector3f(p.position).sub(p.previousPosition);
            velocity.mul(-drag);
            p.accumulatedForce.add(velocity);
        }
    }
}