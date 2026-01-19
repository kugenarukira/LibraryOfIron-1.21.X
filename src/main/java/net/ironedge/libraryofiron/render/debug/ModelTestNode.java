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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

import java.util.List;

public final class ModelTestNode extends RenderNode {

    private final StaticAnchor rootAnchor;
    private final DynamicAnchor childAnchor;
    private final UniversalModelRenderer renderer;

    public ModelTestNode() {
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
        Camera cam = mc.gameRenderer.getMainCamera();
        if (cam == null) return;

        // Root should be 2 blocks above camera/player position
        Vector3f camPos = context.cameraPos();
        rootAnchor.setOffset(new Vector3f(camPos).add(0, 2, 0));

        // Resolve anchors via your existing resolution pipeline
        AnchorResolutionContext arc = new AnchorResolutionContext(mc.getCameraEntity(), context.partialTicks());
        List<ResolvedAnchor> resolved = renderer.resolveAll(arc); // (you should already have this)

        // Draw little crosses at resolved points
        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        for (ResolvedAnchor ra : resolved) {
            drawCross(lines, poseStack, ra.transform().translation(), 0.1f);
        }

        buffers.endBatch(RenderType.lines());
    }

    private static void drawCross(VertexConsumer vc, PoseStack ps, Vector3f p, float r) {
        var pose = ps.last();
        int white = -1;

        vc.addVertex(pose, p.x - r, p.y, p.z).setColor(white).setNormal(1f, 0f, 0f);
        vc.addVertex(pose, p.x + r, p.y, p.z).setColor(white).setNormal(1f, 0f, 0f);

        vc.addVertex(pose, p.x, p.y - r, p.z).setColor(white).setNormal(0f, 1f, 0f);
        vc.addVertex(pose, p.x, p.y + r, p.z).setColor(white).setNormal(0f, 1f, 0f);

        vc.addVertex(pose, p.x, p.y, p.z - r).setColor(white).setNormal(0f, 0f, 1f);
        vc.addVertex(pose, p.x, p.y, p.z + r).setColor(white).setNormal(0f, 0f, 1f);
    }
}
