package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionDebugState;

public final class PhysicsSystem {
    private static final PhysicsSystem INSTANCE = new PhysicsSystem();
    public static PhysicsSystem get() { return INSTANCE; }

    private final java.util.List<PhysicsSimulation> simulations = new java.util.ArrayList<>();

    private PhysicsSystem() {}

    public void add(PhysicsSimulation sim) {
        simulations.add(sim);
    }

    public java.util.List<PhysicsSimulation> simulations() {
        return simulations;
    }

    public PhysicsSimulation getById(String id) {
        if (id == null) return null;
        for (PhysicsSimulation sim : simulations) {
            if (id.equals(sim.id())) return sim;
        }
        return null;
    }

    public void stepAll(net.ironedge.libraryofiron.render.core.FrameContext frame) {
        PhysicsCollisionDebugState.clear();

        float dt = 1.0f / 20.0f;
        for (PhysicsSimulation sim : simulations) {
            sim.step(frame, dt);
        }
    }
}