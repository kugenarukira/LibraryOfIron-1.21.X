package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.EntityHitEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class CombatSystem {

    private final DamageSystem damageSystem = new DamageSystem();

    public void attack(LivingEntity attacker, LivingEntity target, HitProfile hitProfile) {
        // 1. fire EntityHitEvent
        EntityHitEvent hitEvent = new EntityHitEvent(attacker, target, hitProfile);
        LoICore.context().getEventBus().fire(hitEvent);

        // 2. apply damage
        damageSystem.applyDamage(attacker, target, hitProfile.baseDamage());

        // 3. apply knockback/posture, etc. (later)
    }
}