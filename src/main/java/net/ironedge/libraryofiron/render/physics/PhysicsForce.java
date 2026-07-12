package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.core.FrameContext;

public interface PhysicsForce {
    void apply(PhysicsSimulation sim, FrameContext frame, float dt);
}