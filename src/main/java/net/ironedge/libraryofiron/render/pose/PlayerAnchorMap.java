package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class PlayerAnchorMap {

    public record BoneRef(String nodeId, Vector3f localOffset) {}

    private static final float PX = 1f / 16f;
    private static Vector3f px(float x, float y, float z) { return new Vector3f(x * PX, y * PX, z * PX); }

    private static final Map<String, BoneRef> MAP = new HashMap<>();

    static {
        // Direct bones
        MAP.put(AnchorKeys.HEAD.id(),     new BoneRef("Head", new Vector3f()));
        MAP.put(AnchorKeys.SPINE_03.id(), new BoneRef("Body", px(0, 6, 0))); // tweak later

        // Arm origins are shoulders in the PlayerModel
        MAP.put(AnchorKeys.SHOULDER_R.id(), new BoneRef("RightArm", new Vector3f()));
        MAP.put(AnchorKeys.SHOULDER_L.id(), new BoneRef("LeftArm",  new Vector3f()));

        // Hands: 12px down from shoulder, plus your 3px up => -9px.
        // IMPORTANT: Player arms usually extend positive Y downward from the pivot
        // (your model: -2..+10, so hand is +10; using +9 is a good approximation)
        MAP.put(AnchorKeys.HAND_R.id(), new BoneRef("RightArm", px(-1, 8, 0)));
        MAP.put(AnchorKeys.HAND_L.id(), new BoneRef("LeftArm",  px(1, 8, 0)));

        // Legs: origin is hip
        MAP.put(AnchorKeys.HIP_R.id(), new BoneRef("RightLeg", new Vector3f()));
        MAP.put(AnchorKeys.HIP_L.id(), new BoneRef("LeftLeg",  new Vector3f()));

        // Feet: 12px down from hip, 3px up => +9px
        MAP.put(AnchorKeys.FOOT_R.id(), new BoneRef("RightLeg", px(0, 9, 0)));
        MAP.put(AnchorKeys.FOOT_L.id(), new BoneRef("LeftLeg",  px(0, 9, 0)));
    }

    public static BoneRef get(AnchorKey key) {
        return MAP.get(key.id());
    }

    private PlayerAnchorMap() {}
}
