package net.ironedge.libraryofiron.render.umr.geo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GeoOutlineGrouping {

    private GeoOutlineGrouping() {
    }

    public static List<GeoOutlineGroupDef> resolveGroups(GeoRig rig, GeoMesh mesh) {
        if (rig instanceof GeoOutlineGroupedRig groupedRig) {
            List<GeoOutlineGroupDef> custom = groupedRig.outlineGroups();
            if (custom != null && !custom.isEmpty()) {
                return custom;
            }
        }

        return defaultOneGroupPerBone(mesh);
    }

    public static List<GeoOutlineGroupDef> defaultOneGroupPerBone(GeoMesh mesh) {
        List<GeoOutlineGroupDef> groups = new ArrayList<>();

        for (String boneName : mesh.bones.keySet()) {
            groups.add(
                    GeoOutlineGroupDef.builder(boneName, boneName).build()
            );
        }

        return groups;
    }

    public static Map<String, GeoOutlineGroupDef> byRootBone(List<GeoOutlineGroupDef> groups) {
        Map<String, GeoOutlineGroupDef> map = new LinkedHashMap<>();
        for (GeoOutlineGroupDef group : groups) {
            map.put(group.rootBone(), group);
        }
        return map;
    }
}