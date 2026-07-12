package net.ironedge.libraryofiron.render.segmented;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.physics.surface.PhysicsSurfaceView;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;

public final class SurfaceRenderNode extends RenderNode {

    private final String simulationId;
    private final SurfaceTopology topology;
    private final SurfaceMaterial material;

    public SurfaceRenderNode(
            String simulationId,
            SurfaceTopology topology,
            SurfaceMaterial material
    ) {
        super(RenderPhase.DEBUG);
        this.simulationId = simulationId;
        this.topology = topology;
        this.material = material;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;
        if (PhysicsSurfaceView.simulation(simulationId) == null) return;

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

        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                Vector3f a = PhysicsSurfaceView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsSurfaceView.point(simulationId, topology, row + 1, col);
                Vector3f c = PhysicsSurfaceView.point(simulationId, topology, row + 1, col + 1);
                Vector3f d = PhysicsSurfaceView.point(simulationId, topology, row, col + 1);

                if (a == null || b == null || c == null || d == null) continue;

                Vector3f along = new Vector3f(d).sub(a);
                Vector3f across = new Vector3f(b).sub(a);
                Vector3f normal = across.cross(along);
                if (normal.lengthSquared() < 1.0e-8f) {
                    normal.set(0, 1, 0);
                } else {
                    normal.normalize();
                }

                float u0 = (float) row / (float) (topology.rows() - 1);
                float u1 = (float) (row + 1) / (float) (topology.rows() - 1);
                float v0 = (float) col / (float) (topology.cols() - 1);
                float v1 = (float) (col + 1) / (float) (topology.cols() - 1);

                put(vc, pose, a, u0, v0, normal, light, overlay);
                put(vc, pose, b, u1, v0, normal, light, overlay);
                put(vc, pose, c, u1, v1, normal, light, overlay);
                put(vc, pose, d, u0, v1, normal, light, overlay);
            }
        }

        ps.popPose();
        buffers.endBatch(rt);
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