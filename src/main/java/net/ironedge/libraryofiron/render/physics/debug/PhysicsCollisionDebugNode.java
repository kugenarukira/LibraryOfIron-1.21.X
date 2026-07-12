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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class PhysicsCollisionDebugNode extends RenderNode {

    private final String simulationId;
    private final int color;
    private final int ringSegments;
    private final float crossRadius;

    public PhysicsCollisionDebugNode(String simulationId, int color) {
        this(simulationId, color, 12, 0.04f);
    }

    public PhysicsCollisionDebugNode(String simulationId, int color, int ringSegments, float crossRadius) {
        super(RenderPhase.DEBUG);
        this.simulationId = simulationId;
        this.color = color;
        this.ringSegments = ringSegments;
        this.crossRadius = crossRadius;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        PhysicsSimulation sim = PhysicsSystem.get().getById(simulationId);
        if (sim == null) return;
        if (sim.points().isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        for (PhysicsPoint p : sim.points()) {
            Vector3f pos = new Vector3f(p.position);
            float r = p.radius;

            // center cross
            //DebugDraw.cross(vc, ps, pos, crossRadius, color);

            // fake wire sphere
            //DebugDraw.circleXY(vc, ps, pos, r, color, ringSegments);
            //DebugDraw.circleXZ(vc, ps, pos, r, color, ringSegments);
            //DebugDraw.circleYZ(vc, ps, pos, r, color, ringSegments);
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }
}