package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.HealthCap;
import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.combat.events.DamageDealtEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class DamageSystem {

    private static final CapabilityKey<HealthCap> HEALTH_KEY = new CapabilityKey<>("health", HealthCap.class);

    /**
     * Apply damage to a target from an attacker, with optional modifiers.
     */
    public static void applyDamage(LivingEntity attacker, LivingEntity target, float baseDamage) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(HEALTH_KEY)) return;

        HealthCap healthCap = container.get(HEALTH_KEY).getData();
        if (healthCap == null) return;

        // --- Apply status effect modifiers ---
        float finalDamage = baseDamage;

        // Use a proper CapabilityKey for StatusEffectCap
        CapabilityKey<StatusEffectCap> STATUS_KEY = new CapabilityKey<>("status_effects", StatusEffectCap.class);

        if (container.has(STATUS_KEY)) {
            StatusEffectCap statusCap = container.get(STATUS_KEY).getData();
            for (StatusEffectCap.StatusEffect effect : statusCap.getEffects()) {
                finalDamage = modifyDamageForEffect(effect, finalDamage);
            }
        }

        // Apply damage
        healthCap.damage(finalDamage);

        // Fire event
        LoICore.context().getEventBus().fire(new DamageDealtEvent(attacker, target, finalDamage));
    }

    /**
     * Hook to modify damage based on active status effects.
     */
    private static float modifyDamageForEffect(StatusEffectCap.StatusEffect effect, float damage) {
        String effectName = effect.getClass().getSimpleName();

        // Example modifiers:
        if (effectName.equals("Vulnerable")) {
            damage *= 1.25f; // +25% damage
        } else if (effectName.equals("Resilient")) {
            damage *= 0.8f; // -20% damage
        }

        // Can extend with elemental resistances, buffs, debuffs, etc.
        return damage;
    }
}
