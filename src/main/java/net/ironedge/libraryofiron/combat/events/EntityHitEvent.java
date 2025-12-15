package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class EntityHitEvent extends LoIEvent {

    private final LivingEntity attacker;
    private final LivingEntity target;
    private final HitProfile hitProfile;

    public EntityHitEvent(LivingEntity attacker, LivingEntity target, HitProfile hitProfile) {
        this.attacker = attacker;
        this.target = target;
        this.hitProfile = hitProfile;
    }

    public LivingEntity getAttacker() { return attacker; }
    public LivingEntity getTarget() { return target; }
    public HitProfile getHitProfile() { return hitProfile; }
}