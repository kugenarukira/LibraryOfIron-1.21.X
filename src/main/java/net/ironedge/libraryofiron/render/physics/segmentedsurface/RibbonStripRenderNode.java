package net.ironedge.libraryofiron.render.physics.segmentedsurface;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.physics.strip.PhysicsStripView;
import net.ironedge.libraryofiron.render.physics.strip.StripTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;

import java.util.List;

public final class RibbonStripRenderNode extends RenderNode {

    private final String simulationId;
    private final StripTopology topology;
    private final RibbonStripMaterial material;

    public RibbonStripRenderNode(
            String simulationId,
            StripTopology topology,
            RibbonStripMaterial material
    ) {
        super(RenderPhase.DEBUG);
        this.simulationId = simulationId;
        this.topology = topology;
        this.material = material;
    }

    @Override
    public void render(FrameContext frame) {
        if (topology.rows() != 2) return;

        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;
        if (PhysicsStripView.simulation(simulationId) == null) return;

        List<Vector3f> row0 = PhysicsStripView.row(simulationId, topology, 0);
        List<Vector3f> row1 = PhysicsStripView.row(simulationId, topology, 1);

        if (row0.size() != topology.cols() || row1.size() != topology.cols()) return;
        if (row0.size() < 2) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        RenderType rt = material.translucent()
                ? RenderType.entityTranslucent(material.texture())
                : RenderType.entityCutoutNoCull(material.texture());

        VertexConsumer vc = buffers.getBuffer(rt);

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        PoseStack.Pose pose = ps.last();
        int light = LightTexture.FULL_BRIGHT;
        int overlay = OverlayTexture.NO_OVERLAY;

        for (int c = 0; c < topology.cols() - 1; c++) {
            Vector3f a0 = row0.get(c);
            Vector3f a1 = row0.get(c + 1);
            Vector3f b1 = row1.get(c + 1);
            Vector3f b0 = row1.get(c);

            Vector3f along = new Vector3f(a1).sub(a0);
            Vector3f across = new Vector3f(b0).sub(a0);
            Vector3f normal = across.cross(along);
            if (normal.lengthSquared() < 1.0e-8f) {
                normal.set(0, 1, 0);
            } else {
                normal.normalize();
            }

            float u0 = (float) c / (float) (topology.cols() - 1);
            float u1 = (float) (c + 1) / (float) (topology.cols() - 1);

            put(vc, pose, a0, u0, 0f, normal, light, overlay);
            put(vc, pose, a1, u1, 0f, normal, light, overlay);
            put(vc, pose, b1, u1, 1f, normal, light, overlay);
            put(vc, pose, b0, u0, 1f, normal, light, overlay);
        }
        ps.popPose();
        buffers.endBatch(rt);
    }

    private static Vector3f quadNormal(Vector3f a, Vector3f b, Vector3f c) {
        Vector3f ab = new Vector3f(b).sub(a);
        Vector3f ac = new Vector3f(c).sub(a);
        Vector3f n = ab.cross(ac);
        if (n.lengthSquared() < 1.0e-8f) {
            return new Vector3f(0, 1, 0);
        }
        return n.normalize();
    }

    private static void put(
            VertexConsumer vc,
            PoseStack.Pose pose,
            Vector3f p,
            float u,
            float v,
            Vector3f n,
            int light,
            int overlay
    ) {
        vc.addVertex(pose.pose(), p.x, p.y, p.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, n.x, n.y, n.z);
    }
}