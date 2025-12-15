package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.combat.data.DamageType;
import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;


public class DamageDealtEvent extends LoIEvent {

    private final LivingEntity attacker;
    private final LivingEntity target;
    private final float damageAmount;
    private final DamageType damageType = null;

    public DamageDealtEvent(LivingEntity attacker, LivingEntity target, float damageAmount) {
        this.attacker = attacker;
        this.target = target;
        this.damageAmount = damageAmount;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public float getDamageAmount() {
        return damageAmount;
    }

    public DamageType getDamageType() {
        return damageType;
    }
}