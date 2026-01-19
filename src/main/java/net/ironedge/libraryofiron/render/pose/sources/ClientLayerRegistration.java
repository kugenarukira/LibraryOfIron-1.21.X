package net.ironedge.libraryofiron.render.pose.sources;

import net.ironedge.libraryofiron.LibaryofIron;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = LibaryofIron.MODID, value = Dist.CLIENT)
public final class ClientLayerRegistration {

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        System.out.println("[LoI] AddLayers fired, registering PlayerPoseCaptureLayer");
        // Register to all available player skins (default + slim, and any others)
        for (var skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
            if (renderer != null) {
                renderer.addLayer(new PlayerPoseCaptureLayer(renderer));
            }
        }
    }

    private ClientLayerRegistration() {}
}
