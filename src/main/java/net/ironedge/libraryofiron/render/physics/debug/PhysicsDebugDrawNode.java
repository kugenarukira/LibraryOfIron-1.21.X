package net.ironedge.libraryofiron.render.physics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.debug.DebugDraw;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import net.ironedge.libraryofiron.render.physics.verlet.DistanceConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class PhysicsDebugDrawNode extends RenderNode {

    public PhysicsDebugDrawNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        for (PhysicsSimulation sim : PhysicsSystem.get().simulations()) {
            drawSimulation(lines, ps, sim);
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }

    private static void drawSimulation(VertexConsumer lines, PoseStack ps, PhysicsSimulation sim) {
        int pointColor = 0xFFFFFF00;      // yellow
        int pinnedColor = 0xFF00FFFF;     // cyan
        int constraintColor = 0xFFFFFFFF; // white
        for (var c : sim.constraints()) {
            if (c instanceof DistanceConstraint dc) {
                if (dc.a < 0 || dc.a >= sim.points().size()) continue;
                if (dc.b < 0 || dc.b >= sim.points().size()) continue;

                PhysicsPoint a = sim.points().get(dc.a);
                PhysicsPoint b = sim.points().get(dc.b);

                //DebugDraw.line(lines, ps, a.position, b.position, constraintColor);
            }
        }

        for (PhysicsPoint p : sim.points()) {
            //DebugDraw.cross(lines, ps, p.position, 0.15f, p.pinned ? pinnedColor : pointColor);
            //System.out.println("[LoI] point pos = " + p.position + " pinned=" + p.pinned);
        }
    }
}