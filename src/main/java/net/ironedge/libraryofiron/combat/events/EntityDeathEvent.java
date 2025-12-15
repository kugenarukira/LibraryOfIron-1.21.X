package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class EntityDeathEvent extends LoIEvent {

    private final LivingEntity entity;
    private final LivingEntity killer;

    public EntityDeathEvent(LivingEntity entity, LivingEntity killer) {
        this.entity = entity;
        this.killer = killer;
    }

    public LivingEntity getEntity() { return entity; }
    public LivingEntity getKiller() { return killer; }
}