package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Central system for managing combat-related FX logic (particles, sounds, animations)
 * All actual visual/audio calls are deferred to client/network integration.
 */
public final class CombatFXSystem {

    // Optional queue for FX that can be triggered later or batched
    private final List<Runnable> pendingFX = new ArrayList<>();

    /**
     * Called when a normal hit lands.
     */
    public void playHitFX(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        // Record hit FX to pending list
        pendingFX.add(() -> {
            // Hook for custom hit FX logic
            // Example: particle type, color, size, direction
        });
    }

    /**
     * Called when a block succeeds.
     */
    public void playBlockFX(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        pendingFX.add(() -> {
            // Hook for block FX: sparks, sound, flash
        });
    }

    /**
     * Called when a parry succeeds.
     */
    public void playParryFX(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        pendingFX.add(() -> {
            // Hook for parry FX: shield flash, slow-mo, special sound
        });
    }

    /**
     * Called when an entity's posture breaks.
     */
    public void playPostureBreakFX(LivingEntity entity) {
        pendingFX.add(() -> {
            // Hook for posture break FX: stagger animation, shake, particles
        });
    }

    /**
     * Called when a status effect is applied.
     */
    public void playStatusEffectFX(LivingEntity target, StatusEffectCap.StatusEffect effect) {
        pendingFX.add(() -> {
            // Hook for status effect FX: flames, poison clouds, bleed droplets
        });
    }

    /**
     * Called when a special move triggers.
     */
    public void playSpecialMoveFX(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        pendingFX.add(() -> {
            // Hook for cinematic FX: weapon trails, shockwaves, screen shake
        });
    }

    /**
     * Called when an entity dies.
     */
    public void playDeathFX(LivingEntity entity) {
        pendingFX.add(() -> {
            // Hook for death FX: collapse, blood, ragdoll triggers
        });
    }

    /**
     * Called when an entity is downed but not dead.
     */
    public void playDownedFX(LivingEntity entity) {
        pendingFX.add(() -> {
            // Hook for downed FX: stagger animation, fall particle effect
        });
    }

    /**
     * Tick method to process all queued FX hooks.
     * Call this every server tick (or client tick if rendering FX).
     */
    public void tick() {
        if (pendingFX.isEmpty()) return;

        // Execute all queued FX hooks
        for (Runnable fx : pendingFX) {
            fx.run();
        }

        // Clear after execution
        pendingFX.clear();
    }
}
