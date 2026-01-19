package net.ironedge.libraryofiron.render.pose.sources;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PlayerPoseCaptureLayer extends RenderLayer<AvatarRenderState, PlayerModel> {

    private static int counter = 0;

    public PlayerPoseCaptureLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
        super(parent);
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight,
            AvatarRenderState state,
            float p1,
            float p2
    ) {
        PlayerModel model = this.getParentModel();

        // capture world-space transforms using the actual posed stack math
        capturePartWorld("Head", model.head, poseStack);
        capturePartWorld("Body", model.body, poseStack);
        capturePartWorld("RightArm", model.rightArm, poseStack);
        capturePartWorld("LeftArm", model.leftArm, poseStack);
        capturePartWorld("RightLeg", model.rightLeg, poseStack);
        capturePartWorld("LeftLeg", model.leftLeg, poseStack);

        // Debug print (rate-limited)
        counter++;
        if (counter % 240 == 0) {
            System.out.println("[LoI] PlayerPoseCaptureLayer.submit firing");
        }
    }

    private static void capturePartWorld(String nodeId, ModelPart part, PoseStack basePoseStack) {
        // IMPORTANT: basePoseStack is already in camera-relative render space
        basePoseStack.pushPose();
        part.translateAndRotate(basePoseStack);

        Matrix4f m = new Matrix4f(basePoseStack.last().pose());
        basePoseStack.popPose();

        // camera-relative translation from matrix
        Vector3f camRelPos = new Vector3f(m.m30(), m.m31(), m.m32());

        // convert to world-space by adding camera position back
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vector3f worldPos = new Vector3f(camRelPos).add((float) cam.x, (float) cam.y, (float) cam.z);

        Quaternionf rot = new Quaternionf();
        m.getUnnormalizedRotation(rot);

        Vector3f scale = new Vector3f(1, 1, 1);
        m.getScale(scale);

        PoseGraph.get().frame().put(
                new PoseKey("player", nodeId),
                new PoseTransform(worldPos, rot, scale)
        );
    }
}
