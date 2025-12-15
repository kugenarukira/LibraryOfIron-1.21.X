package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class EntityStaggeredEvent extends LoIEvent {

    private final LivingEntity entity;
    private final float previousPosture;

    public EntityStaggeredEvent(LivingEntity entity, float previousPosture) {
        this.entity = entity;
        this.previousPosture = previousPosture;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getPreviousPosture() {
        return previousPosture;
    }
}