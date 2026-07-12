package net.ironedge.libraryofiron.render.physics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.debug.DebugDraw;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsChainView;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsSpline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

import java.util.List;

public final class PhysicsCurveDebugNode extends RenderNode {

    private final String simulationId;
    private final int color;

    public PhysicsCurveDebugNode(String simulationId, int color) {
        super(RenderPhase.DEBUG);
        this.simulationId = simulationId;
        this.color = color;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        List<Vector3f> pts = PhysicsChainView.samplePoints(simulationId);
        pts = PhysicsSpline.catmullRom(pts, 4);
        if (pts.size() < 2) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);
        //System.out.println("[LoI] curve first = " + pts.get(0) + " last = " + pts.get(pts.size() - 1));
        for (int i = 0; i < pts.size() - 1; i++) {
            //DebugDraw.line(vc, ps, pts.get(i), pts.get(i + 1), color);
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }
}