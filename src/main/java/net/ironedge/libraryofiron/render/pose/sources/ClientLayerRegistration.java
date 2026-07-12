package net.ironedge.libraryofiron.render.pose.sources;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientLayerRegistration {

    private static boolean captureRegistered = false;

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        //System.out.println("[LoI] AddLayers fired");

        // ✅ Register capture handler ONCE, on the game bus, on client
        if (!captureRegistered) {
            captureRegistered = true;
            NeoForge.EVENT_BUS.register(net.ironedge.libraryofiron.render.pose.sources.PlayerPoseCaptureEvent.class);
            //System.out.println("[LoI] Registered PlayerPoseCaptureEvent (from AddLayers)");
        }

// Register to all available player skins (default + slim, and any others)
        for (var skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
            if (renderer != null) {
                renderer.addLayer(new PlayerPoseCaptureLayer(renderer));
            }
        }
    }
}
