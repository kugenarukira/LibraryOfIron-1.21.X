package net.ironedge.libraryofiron.render.umr.mesh.debug;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;

public final class MeshMorphDebugNode extends RenderNode {

    public MeshMorphDebugNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        var level = frame.attachment("level", net.minecraft.world.level.Level.class);
        if (level == null) return;

        float t = level.getGameTime() + frame.partialTicks();
        float w = (float) (0.5 + 0.5 * Math.sin(t * 0.08));

        MeshDebugContent.testInstance().morphState().setWeight("bulge", w);
    }
}