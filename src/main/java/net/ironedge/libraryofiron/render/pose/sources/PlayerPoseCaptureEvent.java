package net.ironedge.libraryofiron.render.pose.sources;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Method;

public final class PlayerPoseCaptureEvent {

    @SubscribeEvent
    public static void onLivingPre(RenderLivingEvent.Pre<?, ?, ?> event) {
        //System.out.println("[LoI] PlayerPoseCaptureEvent firing");

        LivingEntityRenderer<?, ?, ?> ler = event.getRenderer();

        PlayerModel model = tryGetPlayerModel(ler);
        if (model == null) return;

        PoseStack ps = event.getPoseStack();
        capturePartWorld("Body", model.body, ps);
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
