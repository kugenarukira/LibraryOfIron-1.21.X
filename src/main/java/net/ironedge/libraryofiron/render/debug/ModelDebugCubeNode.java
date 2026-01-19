package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchor;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchor;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolverRegistry;
import net.ironedge.libraryofiron.render.anchor.resolve.ResolvedAnchor;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.model.graph.ModelPart;
import net.ironedge.libraryofiron.render.model.UniversalModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;

public final class ModelDebugCubeNode extends RenderNode {

    private final StaticAnchor rootAnchor;
    private final DynamicAnchor childAnchor;
    private final UniversalModelRenderer renderer;

    public ModelDebugCubeNode() {
        super(RenderPhase.DEBUG);

        this.rootAnchor = new StaticAnchor(new AnchorKey("root"), new Vector3f(0, 2, 0));
        this.childAnchor = new DynamicAnchor(new AnchorKey("child_dyn"), new Vector3f(0, -1, 0));

        StaticAnchorResolver staticResolver = new StaticAnchorResolver();
        staticResolver.registerAnchor(rootAnchor.key(), rootAnchor.offset());

        DynamicAnchorResolver dynamicResolver = new DynamicAnchorResolver();
        dynamicResolver.registerAnchor(childAnchor.key(), childAnchor.offset());

        AnchorResolverRegistry.registerResolver(AnchorType.STATIC, staticResolver);
        AnchorResolverRegistry.registerResolver(AnchorType.DYNAMIC, dynamicResolver);

        ModelPart rootPart = new ModelPart("RootPart", rootAnchor);
        ModelPart childPart = new ModelPart("ChildPart", childAnchor);
        rootPart.addChild(childPart);

        this.renderer = new UniversalModelRenderer(List.of(rootPart));
    }

    @Override
    public void render(FrameContext context) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = context.attachment("cameraEntity", Entity.class);
        if (cameraEntity == null || mc.level == null) return;

        PoseStack poseStack = context.attachment("poseStack", PoseStack.class);
        if (poseStack == null) return;

        // Put root above head (world-space)
        float height = cameraEntity.getBbHeight();
        rootAnchor.setOffset(new Vector3f(
                (float) cameraEntity.getX(),
                (float) cameraEntity.getY() + height + 2f,
                (float) cameraEntity.getZ()
        ));

        AnchorResolutionContext arc = new AnchorResolutionContext(cameraEntity, context.partialTicks());
        List<ResolvedAnchor> resolved = renderer.resolveAll(arc);
        if (resolved.isEmpty()) return;

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        // Try a filled debug type first; fallback to lines so you *see something*
        RenderType type = pickFilledType();

        int red = 0xFFFF0000;
        int green = 0xFF00FF00;

        HashSet<String> seen = new HashSet<>();
        int i = 0;

        // ✅ Camera-relative transform happens ONCE here
        Vector3f cam = context.cameraPos();
        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        for (ResolvedAnchor ra : resolved) {
            String id = ra.anchor().key().id();
            if (!seen.add(id)) continue;

            Vector3f world = ra.transform().translation();
            int color = (i == 0) ? red : green;

            // ✅ IMPORTANT: get a NEW consumer each time
            VertexConsumer vc = buffers.getBuffer(type);

            DebugSolid.cube(vc, poseStack, world, 0.25f, color);

            // ✅ Flush THIS render type now, so the next cube cannot connect vertices
            buffers.endBatch(type);

            i++;
        }

        poseStack.popPose();

// ❌ REMOVE this, because you already flushed inside the loop:
// buffers.endBatch(type);
    }

    private static RenderType pickFilledType() {
        try {
            var m = RenderType.class.getMethod("debugFilledBox");
            Object out = m.invoke(null);
            if (out instanceof RenderType rt) return rt;
        } catch (Throwable ignored) {}
        // fallback: at least draw *something* while we confirm filled type availability
        return RenderType.lines();
    }

}
