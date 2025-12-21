package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;
import net.ironedge.libraryofiron.combat.data.HitProfile;

public class EntityHitEvent extends LoIEvent {
    private final LivingEntity attacker;
    private final LivingEntity target;
    private final HitProfile hitProfile;
    private boolean canceled = false;

    public EntityHitEvent(LivingEntity attacker, LivingEntity target, HitProfile hitProfile) {
        this.attacker = attacker;
        this.target = target;
        this.hitProfile = hitProfile;
    }

    public LivingEntity getAttacker() { return attacker; }
    public LivingEntity getTarget() { return target; }
    public HitProfile getHitProfile() { return hitProfile; }

    public boolean isCanceled() { return canceled; }
    public void cancel() { canceled = true; }
}
