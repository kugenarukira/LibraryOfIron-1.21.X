package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.umr.UMRModelDef;

public final class SegmentVariant {

    private final String id;
    private final SegmentDef segment;
    private final float length;

    public SegmentVariant(String id, UMRModelDef model, String rootNodeId, String tipNodeId) {
        this.id = id;
        this.segment = new SegmentDef(model, rootNodeId, tipNodeId);
        this.length = this.segment.nominalLength();
    }

    public String id() { return id; }
    public SegmentDef segment() { return segment; }
    public float length() { return length; }
}
