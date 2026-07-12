package net.ironedge.libraryofiron.render.umr.geo;

import java.util.List;

public interface GeoOutlineGroupedRig {

    /**
     * Return custom outline groups for this rig.
     * If empty, the renderer will default to one group per bone.
     */
    List<GeoOutlineGroupDef> outlineGroups();
}