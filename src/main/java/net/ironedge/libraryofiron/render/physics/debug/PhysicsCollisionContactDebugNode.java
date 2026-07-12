package net.ironedge.libraryofiron.render.physics.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.debug.DebugDraw;
import net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionContact;
import net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionDebugState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class PhysicsCollisionContactDebugNode extends RenderNode {

    private final int color;
    private final float crossRadius;
    private final float lineScale;

    public PhysicsCollisionContactDebugNode() {
        this(0xFFFF0000, 0.04f, 1.0f);
    }

    public PhysicsCollisionContactDebugNode(int color, float crossRadius, float lineScale) {
        super(RenderPhase.DEBUG);
        this.color = color;
        this.crossRadius = crossRadius;
        this.lineScale = lineScale;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        var contacts = PhysicsCollisionDebugState.contacts();
        if (contacts.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        for (PhysicsCollisionContact c : contacts) {
            // red cross at closest contact point
            //DebugDraw.cross(vc, ps, c.contactPos, crossRadius, color);

            // red line showing push direction and push magnitude
            Vector3f end = new Vector3f(c.contactPos).add(
                    new Vector3f(c.pushDir).mul(c.pushDist * lineScale)
            );
            //DebugDraw.line(vc, ps, c.contactPos, end, color);
        }

        ps.popPose();
        buffers.endBatch(RenderType.lines());
    }
}