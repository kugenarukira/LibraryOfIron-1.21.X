package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.core.FrameContext;

@FunctionalInterface
public interface PhysicsResetHandler {
    void reset(PhysicsSimulation sim, FrameContext frame);
}