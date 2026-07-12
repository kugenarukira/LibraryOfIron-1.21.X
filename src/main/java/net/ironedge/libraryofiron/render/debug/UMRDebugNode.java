package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.ironedge.libraryofiron.render.umr.UMRModelInstance;
import net.ironedge.libraryofiron.render.umr.UMRModelSystem;
import net.ironedge.libraryofiron.render.umr.UMRNodeDef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class UMRDebugNode extends RenderNode {

    public UMRDebugNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = frame.attachment("poseStack", PoseStack.class);
        if (poseStack == null) return;

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        // draw every UMR node we have for every instance
        for (UMRModelInstance inst : UMRModelSystem.get().instances()) {
            String src = inst.sourceId();

            for (UMRNodeDef node : inst.def().nodes().values()) {
                PoseTransform t = PoseGraph.get().frame().get(new PoseKey(src, node.id()));
                if (t == null) continue;

                Vector3f cam = frame.cameraPos();

                poseStack.pushPose();
                poseStack.translate(t.translation().x - cam.x, t.translation().y - cam.y, t.translation().z - cam.z);
                poseStack.mulPose(t.rotation());
                DebugWire.squareAtOrigin(lines, poseStack, 0.12f, 0xFFFFFF00);
                poseStack.popPose();
            }
        }

        buffers.endBatch(RenderType.lines());
    }


    private void drawNode(VertexConsumer vc, PoseStack ps, FrameContext frame, String srcId, String nodeId, int argb) {
        PoseTransform t = PoseGraph.get().frame().get(new PoseKey(srcId, nodeId));
        if (t == null) return;

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(t.translation().x - cam.x, t.translation().y - cam.y, t.translation().z - cam.z);
        ps.mulPose(t.rotation());

        DebugWire.squareAtOrigin(vc, ps, 0.12f, argb);

        ps.popPose();
    }
}
