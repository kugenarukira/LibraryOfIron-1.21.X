package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.network.combat.*;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.combat.events.EntityHitEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

/**
 * Centralized combat system. Handles attacks, posture, status effects, stances, special moves, and deaths.
 */
public final class CombatSystem {

    private CombatSystem() {} // Prevent instantiation

    /** Perform attack (local or AI call) */
    public static void attack(LivingEntity attacker, LivingEntity target, HitProfile hitProfile) {
        if (attacker == null || target == null || hitProfile == null) return;

        // Fire hit event
        EntityHitEvent hitEvent = new EntityHitEvent(attacker, target, hitProfile);
        LoICore.context().getEventBus().fire(hitEvent);
        if (hitEvent.isCanceled()) return;

        // Fetch stance-based multipliers directly from the entity
        float damageMultiplier = StanceSystem.getDamageMultiplier(attacker);
        float postureMultiplier = StanceSystem.getPostureMultiplier(target);

        // Apply damage & posture
        DamageSystem.applyDamage(attacker, target, hitProfile.baseDamage() * damageMultiplier);
        PostureSystem.applyPostureDamage(target, hitProfile.postureDamage() * postureMultiplier);

        // Apply status effects
        if (hitProfile.statusEffects() != null) {
            for (StatusEffectCap.StatusEffect effect : hitProfile.statusEffects()) {
                StatusEffectSystem.addEffect(target, effect);
            }
        }
    }

    // ========================
    // Server-side payload handlers
    // ========================

    public static void applyHit(LivingEntity attacker, LivingEntity target, HitPayload payload) {
        if (attacker == null || target == null) return;

        float damageMultiplier = StanceSystem.getDamageMultiplier(attacker);
        float postureMultiplier = StanceSystem.getPostureMultiplier(target);

        DamageSystem.applyDamage(attacker, target, payload.damage() * damageMultiplier);
        PostureSystem.applyPostureDamage(target, payload.postureDamage() * postureMultiplier);

        if (payload.statusEffects() != null) {
            for (String effectId : payload.statusEffects()) {
                StatusEffectSystem.addEffect(target, effectId);
            }
        }
    }

    public static void applyParry(LivingEntity target, ParryPayload payload) {
        if (target == null) return;
        if (payload.success()) {
            PostureSystem.applyPostureDamage(target, 10f);
        }
    }

    public static void applyPostureBreak(LivingEntity target, PostureBreakPayload payload) {
        if (target == null) return;
        PostureSystem.applyPostureBreak(target);
    }

    public static void applySpecialMove(LivingEntity attacker, LivingEntity target, SpecialMovePayload payload) {
        if (attacker == null || target == null) return;
        HitProfile profile = SpecialMoveRegistry.get(payload.moveId());
        if (profile == null) return;
        attack(attacker, target, profile);
    }

    public static void applyDeath(LivingEntity target, DeathPayload payload) {
        if (target == null) return;
        target.setHealth(0f);
        PostureSystem.resetPosture(target);
        StatusEffectSystem.clearAll(target);
    }

    public static void applyStatusEffect(LivingEntity target, StatusEffectPayload payload) {
        if (target == null) return;
        if (payload.added()) {
            StatusEffectSystem.addEffect(target, payload.effectId());
        } else {
            StatusEffectSystem.removeEffect(target, payload.effectId());
        }
    }

    // ========================
    // Utility to fetch entity by UUID (server-side)
    // ========================
    public static LivingEntity getEntityFromUUID(UUID uuid) {
        // TODO: Implement server-side lookup
        return null;
    }
}
