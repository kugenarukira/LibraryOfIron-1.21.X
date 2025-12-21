package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.capability.CapabilityHandler;
import net.ironedge.libraryofiron.combat.events.StatusEffectAppliedEvent;
import net.ironedge.libraryofiron.combat.events.StatusEffectRemovedEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles adding, removing, and ticking status effects on entities.
 */
public final class StatusEffectSystem {

    private static final Map<String, StatusEffectFactory> registry = new HashMap<>();

    public static void registerEffect(String id, StatusEffectFactory factory) {
        registry.put(id, factory);
    }

    /** Add effect by ID */
    public static void addEffect(LivingEntity target, String effectId) {
        StatusEffectCap cap = CapabilityHandler.getStatusEffectCap(target);
        if (cap == null) return;

        StatusEffectFactory factory = registry.get(effectId);
        if (factory == null) return;

        StatusEffectCap.StatusEffect effect = factory.create();
        addEffect(target, effect); // delegate to overload
    }

    /** Add effect instance directly */
    public static void addEffect(LivingEntity target, StatusEffectCap.StatusEffect effect) {
        StatusEffectCap cap = CapabilityHandler.getStatusEffectCap(target);
        if (cap == null) return;

        cap.addEffect(effect);
        LoICore.context().getEventBus().fire(new StatusEffectAppliedEvent(target, effect));
    }

    /** Remove first occurrence of effect by class name */
    public static void removeEffect(LivingEntity target, String effectId) {
        StatusEffectCap cap = CapabilityHandler.getStatusEffectCap(target);
        if (cap == null) return;

        StatusEffectCap.StatusEffect toRemove = cap.getEffects().stream()
                .filter(e -> e.getClass().getSimpleName().equals(effectId))
                .findFirst().orElse(null);

        if (toRemove != null) {
            cap.getEffects().remove(toRemove);
            LoICore.context().getEventBus().fire(new StatusEffectRemovedEvent(target, toRemove));
        }
    }

    /** Clear all status effects */
    public static void clearAll(LivingEntity target) {
        StatusEffectCap cap = CapabilityHandler.getStatusEffectCap(target);
        if (cap == null) return;

        for (StatusEffectCap.StatusEffect effect : cap.getEffects()) {
            LoICore.context().getEventBus().fire(new StatusEffectRemovedEvent(target, effect));
        }

        cap.getEffects().clear();
    }

    public interface StatusEffectFactory {
        StatusEffectCap.StatusEffect create();
    }
}
