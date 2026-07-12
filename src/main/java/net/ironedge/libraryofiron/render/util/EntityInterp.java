package net.ironedge.libraryofiron.render.util;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public final class EntityInterp {

    private EntityInterp() {}

    private static boolean loggedMissingPrevPos = false;

    public static Vector3f lerpedPos(Entity e, float pt) {
        // Try common previous-position field names
        Double xo = getDoubleField(e, "xo", "xOld", "xPrev", "x0");
        Double yo = getDoubleField(e, "yo", "yOld", "yPrev", "y0");
        Double zo = getDoubleField(e, "zo", "zOld", "zPrev", "z0");

        double xNow = e.getX();
        double yNow = e.getY();
        double zNow = e.getZ();

        if (xo != null && yo != null && zo != null) {
            float t = clamp01(pt);
            double x = xo + (xNow - xo) * t;
            double y = yo + (yNow - yo) * t;
            double z = zo + (zNow - zo) * t;
            return new Vector3f((float) x, (float) y, (float) z);
        }

        // Log once so we know if elytra jitter is because we can't find prev fields
        if (!loggedMissingPrevPos) {
            loggedMissingPrevPos = true;
        }

        // Fallback (tick position)
        return new Vector3f((float) xNow, (float) yNow, (float) zNow);
    }

    public static float lerpAngleDeg(float pt, float a, float b) {
        float delta = wrapDeg(b - a);
        return a + delta * clamp01(pt);
    }

    public static Float getFloatField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Object v = f.get(obj);
            if (v instanceof Float fl) return fl;
        } catch (Throwable ignored) {}
        return null;
    }

    private static float wrapDeg(float deg) {
        deg = deg % 360f;
        if (deg >= 180f) deg -= 360f;
        if (deg < -180f) deg += 360f;
        return deg;
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : Math.min(v, 1f);
    }

    /** Try multiple field names; return the first that works. */
    private static Double getDoubleField(Object obj, String... names) {
        for (String name : names) {
            Double d = tryGetDoubleField(obj, name);
            if (d != null) return d;
        }
        return null;
    }

    private static Double tryGetDoubleField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Object v = f.get(obj);
            if (v instanceof Double d) return d;
            if (v instanceof Float fl) return (double) fl;
        } catch (Throwable ignored) {}
        return null;
    }
}
