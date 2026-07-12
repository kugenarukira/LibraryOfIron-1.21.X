package net.ironedge.libraryofiron.render.physics.forces;

import org.joml.Vector3f;

public final class WindPreset {

    public final float strength;
    public final float flutter;
    public final Vector3f direction;

    public WindPreset(float strength, float flutter, Vector3f direction) {
        this.strength = strength;
        this.flutter = flutter;
        this.direction = new Vector3f(direction);
    }

    public static WindPreset calm() {
        return new WindPreset(
                0.25f,
                0.6f,
                new Vector3f(0.0f, 0.003f, 1.0f)
        );
    }

    public static WindPreset breeze() {
        return new WindPreset(
                0.5f,
                1.0f,
                new Vector3f(0.1f, 0.005f, 1.0f)
        );
    }

    public static WindPreset gusty() {
        return new WindPreset(
                0.9f,
                2.0f,
                new Vector3f(0.2f, 0.008f, 1.0f)
        );
    }

    public static WindPreset storm() {
        return new WindPreset(
                1.6f,
                3.5f,
                new Vector3f(0.35f, 0.012f, 1.0f)
        );
    }

    public static WindPreset lift() {
        return new WindPreset(
                1.0f,
                1.75f,
                new Vector3f(0.0f, 0.035f, 1.0f)
        );
    }

    public static WindPreset directional(Vector3f direction, float strength, float flutter) {
        return new WindPreset(strength, flutter, direction);
    }
}