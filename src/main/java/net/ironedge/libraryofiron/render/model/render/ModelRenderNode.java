package net.ironedge.libraryofiron.render.model.render;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.model.part.ConditionContext;
import net.ironedge.libraryofiron.render.model.part.PartInstance;
import net.ironedge.libraryofiron.render.model.registry.PartRegistry;
import net.minecraft.world.entity.Entity;

public final class ModelRenderNode extends RenderNode {

    private final PartRegistry registry;

    public ModelRenderNode(PartRegistry registry) {
        super(RenderPhase.MODEL);
        this.registry = registry;
    }

    @Override
    public void render(FrameContext frame) {
        Entity target = frame.attachment("cameraEntity", Entity.class);
        if (target == null) return;

        ConditionContext ctx = new ConditionContext(target, target, false);

        for (PartInstance part : registry.getPartsFor(target)) {
            if (!registry.shouldRender(part, ctx)) continue;
            registry.rendererFor(part).render(part, frame);
        }
    }
}
