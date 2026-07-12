package net.ironedge.libraryofiron.render.umr;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.pose.PoseGraph;

public final class UMRPosePublishNode extends RenderNode {

    public UMRPosePublishNode() {
        super(RenderPhase.PRE_MODEL);
    }

    @Override
    public void render(FrameContext context) {
        // publish UMR instance node poses into PoseGraph for this frame
        UMRModelSystem.get().publishAll(PoseGraph.get());
    }
}
