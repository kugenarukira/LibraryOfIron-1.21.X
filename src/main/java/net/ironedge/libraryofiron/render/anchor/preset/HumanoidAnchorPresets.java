package net.ironedge.libraryofiron.render.anchor.preset;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.util.EntityInterp;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public final class HumanoidAnchorPresets {
    private HumanoidAnchorPresets() {}

    public static AnchorPose resolvePose(Entity e, float pt, AnchorKey key) {
        Vector3f base = EntityInterp.lerpedPos(e, pt);
        float h = e.getBbHeight();

        // Default: entity yaw/pitch, roll=0
        float yaw = lerpedEntityYaw(e, pt);
        float pitch = lerpedEntityPitch(e, pt);
        float roll = 0f;


        // ---------------- HEAD ----------------
        if ("head".equals(key.id()) && e instanceof LivingEntity le) {
            yaw = lerpedHeadYaw(le, pt);
            pitch = lerpedEntityPitch(le, pt);
            roll = 0f;

            return new AnchorPose(
                    new Vector3f(base.x, base.y + h * 0.92f, base.z),
                    yaw, pitch, roll
            );
        }

        // ---------------- SPINE_03 ----------------
        if ("spine_03".equals(key.id()) && e instanceof LivingEntity le) {
            yaw = lerpedBodyYaw(le, pt);

            boolean flightOrSwim = isFallFlyingSafe(le) || isSwimmingSafe(le);
            pitch = flightOrSwim ? lerpedEntityPitch(le, pt) : 0f;
            roll = flightOrSwim ? approximateRollDeg(le) : 0f;

            return new AnchorPose(
                    new Vector3f(base.x, base.y + h * 0.62f, base.z),
                    yaw, pitch, roll
            );
        }
// ---------------- LIMBS (Tier 1.5 using real player pivots) ----------------
// Using your Blockbench pivots (pixels) converted to blocks.
// Coordinate assumption: origin at feet, Y up, units = pixels/16.
// ---------------- LIMBS (Pivot + local) ----------------
        if (e instanceof LivingEntity le) {
            float bodyYaw = lerpedBodyYaw(le, pt);

            boolean flightOrSwim = isFallFlyingSafe(le) || isSwimmingSafe(le);
            float bodyPitch = flightOrSwim ? lerpedEntityPitch(le, pt) : 0f;
            float bodyRoll  = flightOrSwim ? approximateRollDeg(le) : 0f;

            Vector3f pivot;
            Vector3f local;

            // 3px upward adjustment for hands/feet: -12 -> -9
            // (0, -9, 0) in pixels means “3px up”
            Vector3f HAND_LOCAL = px(0, -9, 0);
            Vector3f FOOT_LOCAL = px(0, -9, 0);

            switch (key.id()) {
                // SHOULDERS (pivot only)
                case "shoulder_r" -> { pivot = px( 5, 22, 0); local = px(0, 0, 0); }
                case "shoulder_l" -> { pivot = px(-5, 22, 0); local = px(0, 0, 0); }

                // HANDS (pivot + down)
                case "hand_r" -> { pivot = px( 5, 22, 0); local = HAND_LOCAL; }
                case "hand_l" -> { pivot = px(-5, 22, 0); local = HAND_LOCAL; }

                // HIPS (pivot only)
                case "hip_r" -> { pivot = px( 2, 12, 0); local = px(0, 0, 0); }
                case "hip_l" -> { pivot = px(-2, 12, 0); local = px(0, 0, 0); }

                // FEET (pivot + down)
                case "foot_r" -> { pivot = px( 2, 12, 0); local = FOOT_LOCAL; }
                case "foot_l" -> { pivot = px(-2, 12, 0); local = FOOT_LOCAL; }

                default -> { pivot = null; local = null; }
            }

            if (pivot != null) {
                Vector3f pivotWorld = rotateYawPitchRollDeg(pivot, bodyYaw, bodyPitch, bodyRoll);
                Vector3f localWorld = rotateYawPitchRollDeg(local, bodyYaw, bodyPitch, bodyRoll);

                Vector3f pos = new Vector3f(base).add(pivotWorld).add(localWorld);
                return new AnchorPose(pos, bodyYaw, bodyPitch, bodyRoll);
            }
        }


        // ---------------- LIMBS (Tier 1) ----------------
        if (e instanceof LivingEntity le) {
            float bodyYaw = lerpedBodyYaw(le, pt);

            boolean flightOrSwim = isFallFlyingSafe(le) || isSwimmingSafe(le);
            float bodyPitch = flightOrSwim ? lerpedEntityPitch(le, pt) : 0f;
            float bodyRoll  = flightOrSwim ? approximateRollDeg(le) : 0f;

            // Pivot offsets from FEET origin (in blocks)
            Vector3f pivot;
            Vector3f local;

            switch (key.id()) {
                // ----- SHOULDERS (pivot only) -----
                case "shoulder_r" -> { pivot = px(5, 22, 0);  local = px(0, 0, 0); }
                case "shoulder_l" -> { pivot = px(-5, 22, 0); local = px(0, 0, 0); }

                // ----- HANDS (pivot + local down) -----
                case "hand_r" -> { pivot = px(5, 22, 0);  local = px(0, -12, 0); }
                case "hand_l" -> { pivot = px(-5, 22, 0); local = px(0, -12, 0); }

                // ----- HIPS (pivot only) -----
                case "hip_r" -> { pivot = px(2, 12, 0);  local = px(0, 0, 0); }
                case "hip_l" -> { pivot = px(-2, 12, 0); local = px(0, 0, 0); }

                // ----- FEET (pivot + local down) -----
                // This keeps feet ON ground plane in model space and prevents “below floor”
                case "foot_r" -> { pivot = px(2, 12, 0);  local = px(0, -12, 0); }
                case "foot_l" -> { pivot = px(-2, 12, 0); local = px(0, -12, 0); }

                default -> { pivot = null; local = null; }
            }

            if (pivot != null) {
                // Rotate pivot and local offsets by body pose
                Vector3f pivotWorld = rotateYawPitchRollDeg(pivot, bodyYaw, bodyPitch, bodyRoll);
                Vector3f localWorld = rotateYawPitchRollDeg(local, bodyYaw, bodyPitch, bodyRoll);

                Vector3f pos = new Vector3f(base).add(pivotWorld).add(localWorld);

                // Pose: limbs follow body pose in Tier 1.5
                return new AnchorPose(pos, bodyYaw, bodyPitch, bodyRoll);
            }
        }

        // ---------------- FALLBACK ----------------
        return new AnchorPose(
                new Vector3f(base.x, base.y + h * 0.5f, base.z),
                yaw, pitch, roll
        );
    }


    private static float lerpedEntityYaw(Entity e, float pt) {
        Float yo = EntityInterp.getFloatField(e, "yRotO");
        float yn = e.getYRot();
        return (yo != null) ? EntityInterp.lerpAngleDeg(pt, yo, yn) : yn;
    }

    private static float lerpedEntityPitch(Entity e, float pt) {
        Float xo = EntityInterp.getFloatField(e, "xRotO");
        float xn = e.getXRot();
        return (xo != null) ? EntityInterp.lerpAngleDeg(pt, xo, xn) : xn;
    }

    private static float lerpedHeadYaw(LivingEntity le, float pt) {
        Float ho = EntityInterp.getFloatField(le, "yHeadRotO");
        float hn = le.getYHeadRot();
        return (ho != null) ? EntityInterp.lerpAngleDeg(pt, ho, hn) : hn;
    }

    /**
     * Body yaw interpolation:
     * - Prefer yBodyRotO + yBodyRot fields if accessible
     * - Fallback to entity yaw if mappings hide body fields
     */
    private static float lerpedBodyYaw(LivingEntity le, float pt) {
        Float bo = EntityInterp.getFloatField(le, "yBodyRotO");
        Float bn = tryGetFloatField(le, "yBodyRot");

        if (bo != null && bn != null) {
            return EntityInterp.lerpAngleDeg(pt, bo, bn);
        }

        // fallback: entity yaw
        return lerpedEntityYaw(le, pt);
    }

    private static Float tryGetFloatField(Object obj, String name) {
        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Object v = f.get(obj);
            if (v instanceof Float fl) return fl;
        } catch (Throwable ignored) {}
        return null;
    }

    private static boolean isFallFlyingSafe(LivingEntity le) {
        try {
            return le.isFallFlying();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isSwimmingSafe(LivingEntity le) {
        try {
            return le.isSwimming();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Crude roll approximation for banking during flight.
     * This is intentionally simple; later you can compute roll from look vector vs velocity.
     */
    private static float approximateRollDeg(LivingEntity le) {
        var v = le.getDeltaMovement();
        double horiz = v.x * v.x + v.z * v.z;
        if (horiz < 1.0e-6) return 0f;

        // bank sign based on sideways component
        double bank = Math.max(-1.0, Math.min(1.0, v.x));
        return (float) (bank * 20.0);
    }
    private static Vector3f rightFromYawDeg(float yawDeg) {
        double yawRad = Math.toRadians(yawDeg);
        // Right vector in XZ plane
        return new Vector3f((float) Math.cos(yawRad), 0f, (float) -Math.sin(yawRad));
    }

    private static Vector3f forwardFromYawDeg(float yawDeg) {
        double yawRad = Math.toRadians(yawDeg);
        // Forward vector in XZ plane (Minecraft forward)
        return new Vector3f((float) -Math.sin(yawRad), 0f, (float) -Math.cos(yawRad));
    }
    private static final float PX = 1f / 16f; // pixels to blocks

    private static Vector3f rotateYawPitchRollDeg(Vector3f v, float yawDeg, float pitchDeg, float rollDeg) {
        // Yaw around Y, pitch around X, roll around Z (degrees)
        double yaw = Math.toRadians(-yawDeg);   // note negative to match MC yaw direction
        double pitch = Math.toRadians(pitchDeg);
        double roll = Math.toRadians(rollDeg);

        // Apply yaw
        float x1 = (float) (v.x * Math.cos(yaw) - v.z * Math.sin(yaw));
        float z1 = (float) (v.x * Math.sin(yaw) + v.z * Math.cos(yaw));
        float y1 = v.y;

        // Apply pitch
        float y2 = (float) (y1 * Math.cos(pitch) - z1 * Math.sin(pitch));
        float z2 = (float) (y1 * Math.sin(pitch) + z1 * Math.cos(pitch));
        float x2 = x1;

        // Apply roll
        float x3 = (float) (x2 * Math.cos(roll) - y2 * Math.sin(roll));
        float y3 = (float) (x2 * Math.sin(roll) + y2 * Math.cos(roll));
        float z3 = z2;

        return new Vector3f(x3, y3, z3);
    }

    private static Vector3f px(float x, float y, float z) {
        return new Vector3f(x * PX, y * PX, z * PX);
    }

}
