package net.ironedge.libraryofiron.render.bridge;

import java.util.List;

public record UMRBridgeDef(
        String id,
        Endpoint start,
        Endpoint end,
        List<SegmentVariant> variants,
        int segmentCount,
        SegmentOrdering ordering,
        OrganicMotion organicMotion,
        TipDistanceMode contractedMode,
        TipDistanceMode extendedMode
) {}