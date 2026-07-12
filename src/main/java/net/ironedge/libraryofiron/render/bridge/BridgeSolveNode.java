package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.pose.PoseGraph;

public final class BridgeSolveNode extends RenderNode {

    public BridgeSolveNode() {
        super(RenderPhase.PRE_MODEL);
    }

    @Override
    public void render(FrameContext frame) {
        BridgeSystem.get().solveAll(frame, PoseGraph.get());
    }
}
