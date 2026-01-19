package net.ironedge.libraryofiron.render.core;

import net.ironedge.libraryofiron.LibaryofIron;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.minecraft.client.DeltaTracker;

import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber(modid = LibaryofIron.MODID, value = Dist.CLIENT)
public final class LoIRenderStateCapture {

    private static final AtomicReference<DeltaTracker> LAST_DELTA = new AtomicReference<>();

    private LoIRenderStateCapture() {}

    @SubscribeEvent
    public static void onExtract(ExtractLevelRenderStateEvent event) {
        // This event is fired before RenderLevelStageEvent.* stages.
        // It’s exactly where NeoForge wants you to cache render-state.
        LAST_DELTA.set(event.getDeltaTracker());
    }

    public static DeltaTracker deltaTrackerOrNull() {
        return LAST_DELTA.get();
    }
}
