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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public final class ModelDebugDrawNode extends RenderNode {

    private final StaticAnchor rootAnchor;
    private final DynamicAnchor childAnchor;
    private final UniversalModelRenderer renderer;

    public ModelDebugDrawNode() {
        super(RenderPhase.DEBUG);

        // Anchors
        this.rootAnchor = new StaticAnchor(new AnchorKey("root"), new Vector3f(0, 2, 0));
        this.childAnchor = new DynamicAnchor(new AnchorKey("child_dyn"), new Vector3f(0, -1, 0));

        // Resolvers (register once)
        StaticAnchorResolver staticResolver = new StaticAnchorResolver();
        staticResolver.registerAnchor(rootAnchor.key(), rootAnchor.offset());

        DynamicAnchorResolver dynamicResolver = new DynamicAnchorResolver();
        dynamicResolver.registerAnchor(childAnchor.key(), childAnchor.offset());

        AnchorResolverRegistry.registerResolver(AnchorType.STATIC, staticResolver);
        AnchorResolverRegistry.registerResolver(AnchorType.DYNAMIC, dynamicResolver);

        // Model hierarchy
        ModelPart rootPart = new ModelPart("RootPart", rootAnchor);
        ModelPart childPart = new ModelPart("ChildPart", childAnchor);
        rootPart.addChild(childPart);

        this.renderer = new UniversalModelRenderer(List.of(rootPart));
    }

    @Override
    public void render(FrameContext context) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = context.attachment("cameraEntity", Entity.class);
        if (cameraEntity == null) return;

        // ✅ Proper frame-based interpolation: entity position at partialTicks
        Vector3f basePos = getInterpolatedEntityPos(cameraEntity, context.partialTicks());

        // Put root above HEAD: interpolated feet + bbHeight + 2 blocks
        float height = cameraEntity.getBbHeight();
        rootAnchor.setOffset(new Vector3f(basePos.x, basePos.y + height + 2f, basePos.z));

        AnchorResolutionContext arc = new AnchorResolutionContext(cameraEntity, context.partialTicks());
        List<ResolvedAnchor> resolved = renderer.resolveAll(arc);
        if (resolved.isEmpty()) return;

        PoseStack poseStack = context.attachment("poseStack", PoseStack.class);
        if (poseStack == null) return;

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        int white = -1;

        // translate once into camera-relative space
        poseStack.pushPose();
        Vector3f cam = context.cameraPos();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        // remove duplicates (kills ghost anchors if they ever return)
        java.util.HashSet<String> seen = new java.util.HashSet<>();

        for (ResolvedAnchor ra : resolved) {
            String id = ra.anchor().key().id();
            if (!seen.add(id)) continue;

            Vector3f world = ra.transform().translation();
            DebugDraw.cross(lines, poseStack, world, 0.15f, white);
        }

        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }

    /**
     * Get interpolated entity position for the current render frame.
     * Uses reflection to support mapping differences.
     */
    private static Vector3f getInterpolatedEntityPos(Entity e, float pt) {
        // Try modern interpolation method if present: getPosition(float)
        try {
            var m = e.getClass().getMethod("getPosition", float.class);
            Object out = m.invoke(e, pt);
            if (out instanceof Vec3 v) {
                return new Vector3f((float) v.x, (float) v.y, (float) v.z);
            }
        } catch (Throwable ignored) {}

        // Fallback: non-interpolated tick position
        return new Vector3f((float) e.getX(), (float) e.getY(), (float) e.getZ());
    }
}
