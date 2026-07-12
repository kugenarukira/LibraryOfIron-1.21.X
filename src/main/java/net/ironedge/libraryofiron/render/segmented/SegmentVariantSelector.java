package net.ironedge.libraryofiron.render.segmented;

public interface SegmentVariantSelector {
    SegmentVariantDef select(int segmentIndex, int totalSegments);
}