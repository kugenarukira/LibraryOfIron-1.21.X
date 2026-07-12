package net.ironedge.libraryofiron.render.umr.geo;

import net.ironedge.libraryofiron.render.pose.AnchorRefMap;

import java.util.Map;

public interface GeoRig {
    String poseSpaceId();
    AnchorRefMap anchorMap();
    Map<String, GeoBinding> bindings();

    default boolean visible(String boneName, boolean firstPerson) { return true; }
    default int mask(String boneName) { return GeoRenderMask.DEFAULT.bit; }
}