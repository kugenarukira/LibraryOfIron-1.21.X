package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseTransform;

public interface Endpoint {
    PoseTransform resolve(FrameContext frame);
}
