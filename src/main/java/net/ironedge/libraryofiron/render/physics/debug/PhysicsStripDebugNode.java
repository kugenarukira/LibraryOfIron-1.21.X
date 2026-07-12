package net.ironedge.libraryofiron.render.physics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.debug.DebugDraw;
import net.ironedge.libraryofiron.render.physics.strip.PhysicsStripView;
import net.ironedge.libraryofiron.render.physics.strip.StripTopology;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class PhysicsStripDebugNode extends RenderNode {

    private final String simulationId;
    private final StripTopology topology;
    private final int pointColor;
    private final int edgeColor;
    private final int diagonalColor;

    public PhysicsStripDebugNode(String simulationId, StripTopology topology) {
        this(simulationId, topology, 0xFFFFFF00, 0xFF00FFFF, 0xFFFF00FF);
    }

    public PhysicsStripDebugNode(
            String simulationId,
            StripTopology topology,
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

        if (PhysicsStripView.simulation(simulationId) == null) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        // Draw points
        for (int row = 0; row < topology.rows(); row++) {
            for (int col = 0; col < topology.cols(); col++) {
                Vector3f p = PhysicsStripView.point(simulationId, topology, row, col);
                if (p == null) continue;
                //DebugDraw.cross(vc, ps, p, 0.04f, pointColor);
            }
        }

        // Draw row edges
        for (int row = 0; row < topology.rows(); row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                Vector3f a = PhysicsStripView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsStripView.point(simulationId, topology, row, col + 1);
                if (a == null || b == null) continue;
                //DebugDraw.line(vc, ps, a, b, edgeColor);
            }
        }

        // Draw width edges
        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols(); col++) {
                Vector3f a = PhysicsStripView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsStripView.point(simulationId, topology, row + 1, col);
                if (a == null || b == null) continue;
                //DebugDraw.line(vc, ps, a, b, edgeColor);
            }
        }

        // Draw diagonals
        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                Vector3f a = PhysicsStripView.point(simulationId, topology, row, col);
                Vector3f b = PhysicsStripView.point(simulationId, topology, row + 1, col + 1);
                if (a != null && b != null) {
                    //DebugDraw.line(vc, ps, a, b, diagonalColor);
                }

                Vector3f c = PhysicsStripView.point(simulationId, topology, row + 1, col);
                Vector3f d = PhysicsStripView.point(simulationId, topology, row, col + 1);
                if (c != null && d != null) {
                    //DebugDraw.line(vc, ps, c, d, diagonalColor);
                }
            }
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }
}