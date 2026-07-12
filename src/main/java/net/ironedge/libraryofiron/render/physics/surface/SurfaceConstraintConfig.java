package net.ironedge.libraryofiron.render.physics.surface;

import org.joml.Vector3f;

public final class SurfaceConstraintConfig {

    public float structuralStiffness = 1.0f;
    public float shearStiffness = 0.9f;
    public float bendStiffness = 0.35f;

    public Vector3f gravity = new Vector3f(0f, -0.18f, 0f);
    public float globalDamping = 0.987f;
    public int iterations = 12;

    public boolean selfCollisionEnabled = true;
    public boolean resetOnPerspectiveChange = false;

    public static SurfaceConstraintConfig cloth() {
        SurfaceConstraintConfig c = new SurfaceConstraintConfig();
        c.structuralStiffness = 1.0f;
        c.shearStiffness = 0.85f;
        c.bendStiffness = 0.25f;
        c.gravity.set(0f, -0.18f, 0f);
        c.globalDamping = 0.987f;
        c.iterations = 12;
        return c;
    }

    public static SurfaceConstraintConfig membrane() {
        SurfaceConstraintConfig c = new SurfaceConstraintConfig();
        c.structuralStiffness = 1.0f;
        c.shearStiffness = 0.95f;
        c.bendStiffness = 0.10f;
        c.gravity.set(0f, -0.05f, 0f);
        c.globalDamping = 0.992f;
        c.iterations = 14;
        return c;
    }

    public static SurfaceConstraintConfig looseCloth() {
        SurfaceConstraintConfig c = new SurfaceConstraintConfig();
        c.structuralStiffness = 0.95f;
        c.shearStiffness = 0.70f;
        c.bendStiffness = 0.15f;
        c.gravity.set(0f, -0.22f, 0f);
        c.globalDamping = 0.984f;
        c.iterations = 10;
        return c;
    }
}