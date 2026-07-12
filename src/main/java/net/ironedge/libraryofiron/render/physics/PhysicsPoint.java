package net.ironedge.libraryofiron.render.physics;

import org.joml.Vector3f;

public final class PhysicsPoint {
    public final org.joml.Vector3f position = new org.joml.Vector3f();
    public final org.joml.Vector3f previousPosition = new org.joml.Vector3f();
    public final org.joml.Vector3f accumulatedForce = new org.joml.Vector3f();

    public float inverseMass = 1.0f;
    public float damping = 0.98f;
    public float radius = 0.08f;
    public boolean pinned = false;
    public float pinWeight = 0f; // 0 = free, 1 = fully pinned
    public Vector3f pinTarget = new Vector3f();
}