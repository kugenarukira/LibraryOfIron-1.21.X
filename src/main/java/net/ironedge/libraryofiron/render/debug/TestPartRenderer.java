package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPose;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.anchor.preset.HumanoidAnchorPresets;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.model.part.PartInstance;
import net.ironedge.libraryofiron.render.model.render.PartRenderer;
import net.ironedge.libraryofiron.render.pose.PoseGraphAnchorResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public final class TestPartRenderer implements PartRenderer {

    // Colors (ARGB)
    private static final int RED    = 0xFFFF0000;
    private static final int GREEN  = 0xFF00FF00;
    private static final int BLUE   = 0xFF3FA9F5;
    private static final int YELLOW = 0xFFFFE066;
    private static final int WHITE  = 0xFFFFFFFF;

    @Override
    public void render(PartInstance part, FrameContext frame) {
        Minecraft mc = Minecraft.getInstance();

        PoseStack poseStack = frame.attachment("poseStack", PoseStack.class);
        if (poseStack == null) return;

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        AnchorKey key = part.def().attach().anchor();
        Vector3f cam = frame.cameraPos();
        int color = colorForAnchor(key);
        float radius = radiusForAnchor(key);

        // 1) Try PoseGraph (real ModelPart sync)
        AnchorPoseQ qPose = PoseGraphAnchorResolver.resolvePlayerAnchor(key);

        poseStack.pushPose();

        if (qPose != null) {
            // camera-relative placement
            poseStack.translate(
                    qPose.pos().x - cam.x,
                    qPose.pos().y - cam.y,
                    qPose.pos().z - cam.z
            );

            // apply quaternion rotation directly
            poseStack.mulPose(qPose.rot());

        } else {
            // 2) Fallback: preset math
            AnchorPose pose = HumanoidAnchorPresets.resolvePose(part.owner(), frame.partialTicks(), key);

            poseStack.translate(
                    pose.pos().x - cam.x,
                    pose.pos().y - cam.y,
                    pose.pos().z - cam.z
            );

            poseStack.mulPose(Axis.YP.rotationDegrees(pose.yawDeg()));
            poseStack.mulPose(Axis.XP.rotationDegrees(pose.pitchDeg()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pose.rollDeg()));
        }

        DebugWire.squareAtOrigin(lines, poseStack, radius, color);

        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }

    private static float radiusForAnchor(AnchorKey key) {
        String id = key.id();

        // hands/feet: 1.5x
        if (id.equals("hand_l") || id.equals("hand_r") || id.equals("foot_l") || id.equals("foot_r")) return 0.12f;

        // shoulders/hips: 2x
        if (id.equals("shoulder_l") || id.equals("shoulder_r") || id.equals("hip_l") || id.equals("hip_r")) return 0.20f;

        // head/chest: 2x
        if (id.equals("head") || id.equals("spine_03") || id.equals("chest")) return 0.28f;

        return 0.12f;
    }


    private static int colorForAnchor(AnchorKey key) {
        String id = key.id();

        if (id.equals("spine_03")) return RED;
        if (id.equals("head")) return GREEN;

        if (id.equals("shoulder_l") || id.equals("shoulder_r") || id.equals("hip_l") || id.equals("hip_r")) {
            return BLUE;
        }

        if (id.equals("hand_l") || id.equals("hand_r") || id.equals("foot_l") || id.equals("foot_r")) {
            return YELLOW;
        }

        return WHITE;
    }
}
