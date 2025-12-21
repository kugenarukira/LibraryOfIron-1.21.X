package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.*;
import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.*;
import net.ironedge.libraryofiron.event.events.*;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class CombatPipeline {

    private final DamageSystem damageSystem = new DamageSystem();
    private final PostureSystem postureSystem = new PostureSystem();
    private final ComboSystem comboSystem = new ComboSystem();
    private final BlockSystem blockSystem = new BlockSystem();
    private final ParrySystem parrySystem = new ParrySystem();
    private final HitResolutionSystem hitSystem =
            new HitResolutionSystem(comboSystem, blockSystem, parrySystem);
    private final WoundSystem woundSystem = new WoundSystem();
    private final CombatFXSystem fxSystem = new CombatFXSystem(); // NEW: handles visuals/sounds

    private static final CapabilityKey<HealthCap> HEALTH_KEY =
            new CapabilityKey<>("health", HealthCap.class);
    private static final CapabilityKey<PostureCap> POSTURE_KEY =
            new CapabilityKey<>("posture", PostureCap.class);
    private static final CapabilityKey<StatusEffectCap> STATUS_EFFECT_KEY =
            new CapabilityKey<>("status_effects", StatusEffectCap.class);

    public void executeHit(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        if (attacker == null || target == null || hit == null) return;
        if (target.isDeadOrDying()) return;

        /* ---------------- PARRY ---------------- */
        boolean parried = parrySystem.tryParry(attacker, target, hit);
        if (parried) {
            applyParryRefund(attacker, hit);
            comboSystem.resetCombo(attacker, hit.id());
            fxSystem.playParryFX(attacker, target, hit); // trigger FX
            return;
        }

        /* ---------------- BLOCK ---------------- */
        boolean blocked = blockSystem.tryBlock(attacker, target, hit);
        if (blocked) {
            comboSystem.resetCombo(attacker, hit.id());
            fxSystem.playBlockFX(attacker, target, hit); // trigger FX
            return;
        }

        /* ---------------- HIT ---------------- */
        comboSystem.registerHit(attacker, hit.id());
        damageSystem.applyDamage(attacker, target, hit.baseDamage());
        postureSystem.applyPostureDamage(target, hit.postureDamage());
        checkPostureBreak(target);

        LoICore.context().getEventBus().fire(new EntityHitEvent(attacker, target, hit));
        fxSystem.playHitFX(attacker, target, hit); // trigger FX
        applyHitStatusEffects(target, hit);

        /* ---------------- SPECIAL MOVE ---------------- */
        if (comboSystem.hasReachedSpecialThreshold(attacker, hit.id())) {
            comboSystem.triggerSpecialMove(attacker, target, hit.id());
            fxSystem.playSpecialMoveFX(attacker, target, hit); // trigger FX
        }

        /* ---------------- DEATH / DOWNED ---------------- */
        checkDeathOrDowned(target, attacker);
    }

    private void applyParryRefund(LivingEntity attacker, HitProfile hit) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(attacker);
        if (container == null) return;

        if (container.has(BlockSystem.STAMINA_KEY)) {
            StaminaCap stamina = container.get(BlockSystem.STAMINA_KEY).getData();
            if (stamina != null) {
                stamina.add(hit.parryStaminaRefund());
            }
        }

        if (container.has(POSTURE_KEY)) {
            PostureCap posture = container.get(POSTURE_KEY).getData();
            if (posture != null) {
                posture.takeDamage(hit.parryPostureDamage());
                if (posture.isBroken()) {
                    LoICore.context().getEventBus().fire(new PostureBreakEvent(attacker));
                    fxSystem.playPostureBreakFX(attacker); // trigger FX
                }
            }
        }
    }

    private void checkPostureBreak(LivingEntity target) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(POSTURE_KEY)) return;

        PostureCap posture = container.get(POSTURE_KEY).getData();
        if (posture != null && posture.isBroken()) {
            LoICore.context().getEventBus().fire(new PostureBreakEvent(target));
            fxSystem.playPostureBreakFX(target); // trigger FX
        }
    }

    private void applyHitStatusEffects(LivingEntity target, HitProfile hit) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(STATUS_EFFECT_KEY)) return;

        StatusEffectCap statusCap = container.get(STATUS_EFFECT_KEY).getData();
        if (statusCap == null) return;

        for (StatusEffectCap.StatusEffect effect : hit.statusEffects()) {
            statusCap.addEffect(effect);
            LoICore.context().getEventBus().fire(new StatusEffectAppliedEvent(target, effect));
            fxSystem.playStatusEffectFX(target, effect); // trigger FX
        }
    }

    private void checkDeathOrDowned(LivingEntity target, LivingEntity attacker) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(HEALTH_KEY)) return;

        HealthCap health = container.get(HEALTH_KEY).getData();
        if (health == null) return;

        if (health.isDead()) {
            LoICore.context().getEventBus().fire(new EntityDeathEvent(target, attacker));
            fxSystem.playDeathFX(target); // trigger FX
        } else if (health.isDowned()) {
            LoICore.context().getEventBus().fire(new EntityDownedEvent(target, attacker));
            fxSystem.playDownedFX(target); // trigger FX
        }
    }
}
