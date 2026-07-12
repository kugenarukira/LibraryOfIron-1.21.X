package net.ironedge.libraryofiron.render.physics;

public interface PhysicsConstraint {
    void solve(PhysicsSimulation sim, float dt);
}