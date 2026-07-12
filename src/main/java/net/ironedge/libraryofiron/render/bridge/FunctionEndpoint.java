package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseTransform;

import java.util.function.Function;

public final class FunctionEndpoint implements Endpoint {

    private final Function<FrameContext, PoseTransform> fn;

    public FunctionEndpoint(Function<FrameContext, PoseTransform> fn) {
        this.fn = fn;
    }

    @Override
    public PoseTransform resolve(FrameContext frame) {
        return fn.apply(frame);
    }
}
