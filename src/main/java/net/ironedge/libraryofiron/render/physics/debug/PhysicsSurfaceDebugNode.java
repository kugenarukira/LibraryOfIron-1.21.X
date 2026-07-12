package net.ironedge.libraryofiron.render.physics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.debug.DebugDraw;
import net.ironedge.libraryofiron.render.physics.surface.PhysicsSurfaceView;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class PhysicsSurfaceDebugNode extends RenderNode {

    private final String simulationId;
    private final SurfaceTopology topology;
    private final int pointColor;
    private final int edgeColor;
    private final int diagonalColor;

    public PhysicsSurfaceDebugNode(String simulationId, SurfaceTopology topology) {
        this(simulationId, topology, 0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF);
    }

    public PhysicsSurfaceDebugNode(
            String simulationId,
            SurfaceTopology topology,
            int pointColor,
            int edgeColor,
            int diagonalColor
    ) {
        super(RenderPhase.DEBUG);
        this.simulationId = simulationId;
        this.topology = topology;
        this.pointColor = pointColor;
        this.edgeColor = edgeColor;
        this.diagonalColor = diagonalColor;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;
        if (PhysicsSurfaceView.simulation(simulationId) == null) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        // points
        for (int row = 0; row < topology.rows(); row++) {
            for (int col = 0; col < topology.cols(); col++) {
                Vector3f p = PhysicsSurfaceView.point(simulationId, topology, row, col);
                if (p == null) continue;
                //DebugDraw.cross(vc, ps, p, 0.035f, pointColor);
            }
        }

        // horizontal edges
        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols(); col++) {
                Vector3f a = PhysicsSurfaceView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsSurfaceView.point(simulationId, topology, row + 1, col);
                if (a == null || b == null) continue;
               // DebugDraw.line(vc, ps, a, b, edgeColor);
            }
        }

        // vertical edges
        for (int row = 0; row < topology.rows(); row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                Vector3f a = PhysicsSurfaceView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsSurfaceView.point(simulationId, topology, row, col + 1);
                if (a == null || b == null) continue;
                //DebugDraw.line(vc, ps, a, b, edgeColor);
            }
        }

        // diagonals
        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                Vector3f a = PhysicsSurfaceView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsSurfaceView.point(simulationId, topology, row + 1, col + 1);
                if (a != null && b != null) {
                    //DebugDraw.line(vc, ps, a, b, diagonalColor);
                }

                Vector3f c = PhysicsSurfaceView.point(simulationId, topology, row + 1, col);
                Vector3f d = PhysicsSurfaceView.point(simulationId, topology, row, col + 1);
                if (c != null && d != null) {
                    //DebugDraw.line(vc, ps, c, d, diagonalColor);
                }
            }
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }
}