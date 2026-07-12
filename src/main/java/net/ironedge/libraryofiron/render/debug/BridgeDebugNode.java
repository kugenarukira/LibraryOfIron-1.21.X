package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.bridge.BridgeSystem;
import net.ironedge.libraryofiron.render.bridge.SegmentVariant;
import net.ironedge.libraryofiron.render.bridge.UMRBridgeInstance;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class BridgeDebugNode extends RenderNode {

    public BridgeDebugNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        for (UMRBridgeInstance b : BridgeSystem.get().bridges()) {
            for (int i = 0; i < b.segmentCount(); i++) {
                String src = b.segmentSourceId(i);
                SegmentVariant v = b.variantAt(i);

                int rootColor = variantColorFromSource(src);
                int tipColor = 0xFFFFFFFF;

                drawNode(lines, ps, cam, src, v.segment().rootNodeId(), rootColor);
                drawNode(lines, ps, cam, src, v.segment().tipNodeId(), tipColor);
            }
        }

        buffers.endBatch(RenderType.lines());
    }

    private int variantColorFromSource(String src) {
        // src looks like "umr:bridge:<variantId>:seg<i>"
        // Example: "umr:bridge:short:seg3"
        // We'll just search for ":short:" / ":med:" / ":long:"
        if (src.contains(":short:")) return 0xFF3FA9F5; // blue
        if (src.contains(":med:"))   return 0xFF00FF00; // green
        if (src.contains(":long:"))  return 0xFFFF0000; // red
        return 0xFFFFFF00; // fallback yellow
    }

    private void drawNode(VertexConsumer vc, PoseStack ps, Vector3f cam, String src, String nodeId, int color) {
        PoseTransform t = PoseGraph.get().frame().get(new PoseKey(src, nodeId));
        if (t == null) return;

        ps.pushPose();
        ps.translate(t.translation().x - cam.x, t.translation().y - cam.y, t.translation().z - cam.z);
        ps.mulPose(t.rotation());
        DebugWire.squareAtOrigin(vc, ps, 0.08f, color);
        ps.popPose();
    }
}
