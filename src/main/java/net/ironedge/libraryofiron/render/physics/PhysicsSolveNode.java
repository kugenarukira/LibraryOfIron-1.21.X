package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;

public final class PhysicsSolveNode extends RenderNode {

    public PhysicsSolveNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        PhysicsSystem.get().stepAll(frame);
    }
}