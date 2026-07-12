package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;

public final class PoseKeyEndpoint implements Endpoint {

    private final PoseKey key;

    public PoseKeyEndpoint(PoseKey key) {
        this.key = key;
    }

    @Override
    public PoseTransform resolve(FrameContext frame) {
        PoseTransform t = PoseGraph.get().frame().get(key);
        return (t != null) ? t : null;
    }
}
