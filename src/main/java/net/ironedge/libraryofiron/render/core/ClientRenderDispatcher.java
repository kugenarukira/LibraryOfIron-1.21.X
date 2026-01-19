package net.ironedge.libraryofiron.render.core;

import net.ironedge.libraryofiron.LibaryofIron;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@EventBusSubscriber(modid = LibaryofIron.MODID, value = Dist.CLIENT)
public final class ClientRenderDispatcher {

    private static final RenderEngine ENGINE = new RenderEngine();

    static {
        RenderBootstrap.install(ENGINE);
    }

    public static RenderEngine engine() {
        return ENGINE;
    }

    /**
     * 🔹 FRAME BEGIN
     * This MUST run once per frame, before any pose capture happens.
     */
    @SubscribeEvent
    public static void onAfterSky(RenderLevelStageEvent.AfterSky event) {
        PoseGraph.get().beginFrame();
    }

    /**
     * 🔹 UMR RENDER PASS
     * Runs AFTER entities are rendered, so:
     * - PlayerPoseCaptureEvent has already populated PoseGraph
     * - Armor / layers / animations are final for this frame
     */
    @SubscribeEvent
    public static void onAfterEntities(RenderLevelStageEvent.AfterEntities event) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Entity cameraEntity = mc.getCameraEntity();
        Camera camera = mc.gameRenderer.getMainCamera();

        if (level == null || cameraEntity == null || camera == null) return;

        float partialTicks = 0f;
        DeltaTracker dt = LoIRenderStateCapture.deltaTrackerOrNull();
        if (dt != null) {
            partialTicks = dt.getGameTimeDeltaPartialTick(false);
        }

        // View matrix from vanilla render
        Matrix4f view = new Matrix4f(event.getModelViewMatrix());

        // Projection not yet exposed — identity for now
        Matrix4f proj = new Matrix4f();

        Vector3f camPos = new Vector3f(
                (float) camera.getPosition().x,
                (float) camera.getPosition().y,
                (float) camera.getPosition().z
        );

        FrameContext ctx = new FrameContext(partialTicks, view, proj, camPos)
                .attach("level", level)
                .attach("cameraEntity", cameraEntity)
                .attach("camera", camera)
                .attach("poseStack", event.getPoseStack())
                .attach("levelRenderer", event.getLevelRenderer())
                .attach("levelRenderState", event.getLevelRenderState());



        ENGINE.renderFrame(ctx);
    }

    private ClientRenderDispatcher() {}
}
