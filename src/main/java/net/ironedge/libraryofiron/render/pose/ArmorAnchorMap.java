package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class ArmorAnchorMap implements AnchorRefMap {

    public static final ArmorAnchorMap INSTANCE = new ArmorAnchorMap();

    private static final float PX = 1f / 16f;
    private static Vector3f px(float x, float y, float z) { return new Vector3f(x * PX, y * PX, z * PX); }

    private final Map<String, BoneRef> map = new HashMap<>();

    private ArmorAnchorMap() {
        map.put(AnchorKeys.ARMORHEAD.id(),       new BoneRef("Head", new Vector3f()));
        map.put(AnchorKeys.ARMORTORSO.id(),      new BoneRef("Body", new Vector3f()));
        map.put(AnchorKeys.ARMORHIPS.id(),       new BoneRef("Body", px(0, 11, 0)));

        map.put(AnchorKeys.ARMORSHOULDER_R.id(), new BoneRef("RightArm", new Vector3f()));
        map.put(AnchorKeys.ARMORSHOULDER_L.id(), new BoneRef("LeftArm",  new Vector3f()));

        map.put(AnchorKeys.ARMORHAND_R.id(),     new BoneRef("RightArm", px(-1, 8, 0)));
        map.put(AnchorKeys.ARMORHAND_L.id(),     new BoneRef("LeftArm",  px(1, 8, 0)));

        map.put(AnchorKeys.ARMORHIP_R.id(),      new BoneRef("RightLeg", new Vector3f()));
        map.put(AnchorKeys.ARMORHIP_L.id(),      new BoneRef("LeftLeg",  new Vector3f()));

        map.put(AnchorKeys.ARMORFOOT_R.id(),     new BoneRef("RightLeg", new Vector3f()));
        map.put(AnchorKeys.ARMORFOOT_L.id(),     new BoneRef("LeftLeg",  new Vector3f()));
    }

    @Override
    public BoneRef get(AnchorKey key) {
        return map.get(key.id());
    }
}