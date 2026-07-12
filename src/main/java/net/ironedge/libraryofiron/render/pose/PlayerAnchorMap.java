package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class PlayerAnchorMap implements AnchorRefMap {

    public static final PlayerAnchorMap INSTANCE = new PlayerAnchorMap();

    private static final float PX = 1f / 16f;
    private static Vector3f px(float x, float y, float z) { return new Vector3f(x * PX, y * PX, z * PX); }

    private final Map<String, BoneRef> map = new HashMap<>();

    private PlayerAnchorMap() {
        map.put(AnchorKeys.HEAD.id(),       new BoneRef("Head", new Vector3f()));
        map.put(AnchorKeys.TORSO.id(),      new BoneRef("Body", new Vector3f()));
        map.put(AnchorKeys.HIPS.id(),       new BoneRef("Body", px(0, 11, 0)));

        map.put(AnchorKeys.SHOULDER_R.id(), new BoneRef("RightArm", new Vector3f()));
        map.put(AnchorKeys.SHOULDER_L.id(), new BoneRef("LeftArm",  new Vector3f()));

        map.put(AnchorKeys.HAND_R.id(),     new BoneRef("RightArm", px(-1, 8, 0)));
        map.put(AnchorKeys.HAND_L.id(),     new BoneRef("LeftArm",  px(1, 8, 0)));

        map.put(AnchorKeys.HIP_R.id(),      new BoneRef("RightLeg", new Vector3f()));
        map.put(AnchorKeys.HIP_L.id(),      new BoneRef("LeftLeg",  new Vector3f()));

        map.put(AnchorKeys.FOOT_R.id(),     new BoneRef("RightLeg", px(0, 9, 0)));
        map.put(AnchorKeys.FOOT_L.id(),     new BoneRef("LeftLeg",  px(0, 9, 0)));
    }

    @Override
    public BoneRef get(AnchorKey key) {
        return map.get(key.id());
    }
}