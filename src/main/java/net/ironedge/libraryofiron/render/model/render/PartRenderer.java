package net.ironedge.libraryofiron.render.model.render;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.model.part.PartInstance;

public interface PartRenderer {
    void render(PartInstance part, FrameContext frame);
}
