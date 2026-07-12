package net.ironedge.libraryofiron.render.debug.rigs;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.ArmorAnchorMap;
import net.ironedge.libraryofiron.render.umr.geo.GeoBinding;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineGroupDef;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineGroupedRig;
import net.ironedge.libraryofiron.render.umr.geo.GeoRig;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ironedge.libraryofiron.render.umr.geo.GeoBaker.PX;

public final class DebugMBodyRig implements GeoRig, GeoOutlineGroupedRig {

    private static Vector3f px(float x, float y, float z) {
        return new Vector3f(x * PX, y * PX, z * PX);
    }

    @Override
    public String poseSpaceId() {
        return "player";
    }

    @Override
    public AnchorRefMap anchorMap() {
        return ArmorAnchorMap.INSTANCE;
    }

    @Override
    public Map<String, GeoBinding> bindings() {
        var m = new HashMap<String, GeoBinding>();

        m.put("armorHead", GeoBinding.anchorOnly(AnchorKeys.ARMORHEAD));
        m.put("armorBody", GeoBinding.anchorOnly(AnchorKeys.ARMORTORSO));
        m.put("armorLeftArm", GeoBinding.anchorOnly(AnchorKeys.ARMORSHOULDER_L));
        m.put("armorRightArm", GeoBinding.anchorOnly(AnchorKeys.ARMORSHOULDER_R));
        m.put("armorLeftLeg", GeoBinding.anchorOnly(AnchorKeys.ARMORHIP_L));
        m.put("armorRightLeg", GeoBinding.anchorOnly(AnchorKeys.ARMORHIP_R));
        m.put("armorLeftBoot", GeoBinding.anchorOnly(AnchorKeys.ARMORFOOT_L));
        m.put("armorRightBoot", GeoBinding.anchorOnly(AnchorKeys.ARMORFOOT_R));

        return m;
    }

    @Override
    public List<GeoOutlineGroupDef> outlineGroups() {
        return List.of(
                GeoOutlineGroupDef.builder("head", "armorHead").build(),
                GeoOutlineGroupDef.builder("body", "armorBody").build(),
                GeoOutlineGroupDef.builder("left_arm", "armorLeftArm").build(),
                GeoOutlineGroupDef.builder("right_arm", "armorRightArm").build(),
                GeoOutlineGroupDef.builder("left_leg", "armorLeftLeg").build(),
                GeoOutlineGroupDef.builder("right_leg", "armorRightLeg").build(),
                GeoOutlineGroupDef.builder("left_boot", "armorLeftBoot").build(),
                GeoOutlineGroupDef.builder("right_boot", "armorRightBoot").build()
        );
    }
}