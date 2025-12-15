package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.combat.events.StatusEffectAppliedEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class WoundSystem {

    private static final CapabilityKey<StatusEffectCap> STATUS_EFFECT_KEY =
            new CapabilityKey<>("status_effects", StatusEffectCap.class);

    public void applyWound(LivingEntity target, StatusEffectCap.StatusEffect effect) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(STATUS_EFFECT_KEY)) return;

        StatusEffectCap statusCap = container.get(STATUS_EFFECT_KEY).getData();
        if (statusCap == null) return;

        statusCap.addEffect(effect);
        LoICore.context().getEventBus().fire(new StatusEffectAppliedEvent(target, effect));
    }
}
