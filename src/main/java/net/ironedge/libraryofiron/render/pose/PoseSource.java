package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.core.FrameContext;

public interface PoseSource {
    /** Called once per frame to populate PoseGraph */
    void capture(FrameContext frame, PoseGraph graph);
}
