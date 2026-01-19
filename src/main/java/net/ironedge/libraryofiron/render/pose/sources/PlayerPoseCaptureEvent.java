package net.ironedge.libraryofiron.render.pose.sources;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ironedge.libraryofiron.LibaryofIron;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Method;

@EventBusSubscriber(modid = LibaryofIron.MODID, value = Dist.CLIENT)
public final class PlayerPoseCaptureEvent {

    private static int counter = 0;

    @SubscribeEvent
    public static void onLivingPre(RenderLivingEvent.Pre<?, ?, ?> event) {
        // Only player render states
        if (!(event.getRenderState() instanceof AvatarRenderState)) return;
        if (!(event.getRenderer() instanceof LivingEntityRenderer<?, ?, ?> ler)) return;

        PlayerModel model = tryGetPlayerModel(ler);
        if (model == null) return;

        PoseStack ps = event.getPoseStack();

        // ✅ HERE is where you call capturePartWorld(...)
        capturePartWorld("Head", model.head, ps);
        capturePartWorld("Body", model.body, ps);
        capturePartWorld("RightArm", model.rightArm, ps);
        capturePartWorld("LeftArm", model.leftArm, ps);
        capturePartWorld("RightLeg", model.rightLeg, ps);
        capturePartWorld("LeftLeg", model.leftLeg, ps);

        // Debug print (rate-limited)
        counter++;
        if (counter % 240 == 0) {
            System.out.println("[LoI] PlayerPoseCaptureEvent firing");
        }
    }

    private static PlayerModel tryGetPlayerModel(LivingEntityRenderer<?, ?, ?> ler) {
        try {
            Method m = ler.getClass().getMethod("getModel");
            Object out = m.invoke(ler);
            return (out instanceof PlayerModel pm) ? pm : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void capturePartWorld(String nodeId, ModelPart part, PoseStack basePoseStack) {
        basePoseStack.pushPose();
        part.translateAndRotate(basePoseStack);

        Matrix4f m = new Matrix4f(basePoseStack.last().pose());
        basePoseStack.popPose();

        Vector3f camRelPos = new Vector3f(m.m30(), m.m31(), m.m32());

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

    private PlayerPoseCaptureEvent() {}
}
