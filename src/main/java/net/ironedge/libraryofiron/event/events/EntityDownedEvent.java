package net.ironedge.libraryofiron.event.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class EntityDownedEvent extends LoIEvent {
    private final LivingEntity target;
    private final LivingEntity attacker;
    public EntityDownedEvent(LivingEntity target, LivingEntity attacker) { this.target = target; this.attacker = attacker; }
    public LivingEntity getTarget() { return target; }
    public LivingEntity getAttacker() { return attacker; }
}