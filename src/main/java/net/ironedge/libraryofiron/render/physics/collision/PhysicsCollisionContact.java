package net.ironedge.libraryofiron.render.physics.collision;

import org.joml.Vector3f;

public final class PhysicsCollisionContact {
    public final Vector3f pointPos;
    public final Vector3f contactPos;
    public final Vector3f pushDir;
    public final float pushDist;

    public PhysicsCollisionContact(Vector3f pointPos, Vector3f contactPos, Vector3f pushDir, float pushDist) {
        this.pointPos = pointPos;
        this.contactPos = contactPos;
        this.pushDir = pushDir;
        this.pushDist = pushDist;
    }
}