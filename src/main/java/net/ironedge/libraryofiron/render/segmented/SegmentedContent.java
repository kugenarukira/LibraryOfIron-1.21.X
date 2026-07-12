package net.ironedge.libraryofiron.render.segmented;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.debug.rigs.AmatsuTestRig;

public class SegmentedContent {
    private SegmentedContent(){}

    public static void install(RenderEngine engine) {
        engine.graph().addNode(new SegmentedRenderNode(new AmatsuTestRig()));
    }
}
