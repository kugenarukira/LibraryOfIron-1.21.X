package net.ironedge.libraryofiron.render.debug;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;

public final class DebugPrintNode extends RenderNode {

    private int frames = 0;

    public DebugPrintNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext context) {
        frames++;
        if (frames % 60 == 0) {
            System.out.println("[LoI] Render frame event is running.");
        }
    }
}
