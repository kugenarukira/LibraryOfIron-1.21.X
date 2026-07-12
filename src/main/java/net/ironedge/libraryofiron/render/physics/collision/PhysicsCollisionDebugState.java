package net.ironedge.libraryofiron.render.physics.collision;

import java.util.ArrayList;
import java.util.List;

public final class PhysicsCollisionDebugState {
    private static final List<PhysicsCollisionContact> CONTACTS = new ArrayList<>();

    private PhysicsCollisionDebugState() {}

    public static void clear() {
        CONTACTS.clear();
    }

    public static void add(PhysicsCollisionContact contact) {
        CONTACTS.add(contact);
    }

    public static List<PhysicsCollisionContact> contacts() {
        return CONTACTS;
    }
}