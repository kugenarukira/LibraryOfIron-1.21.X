package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public final class SpecialMoveEvent extends LoIEvent {

    private final LivingEntity executor;
    private final String moveId;
    private final LivingEntity target;

    public SpecialMoveEvent(LivingEntity executor, String moveId, LivingEntity target) {
        this.executor = executor;
        this.moveId = moveId;
        this.target = target;
    }

    public LivingEntity getExecutor() {
        return executor;
    }

    public String getMoveId() {
        return moveId;
    }

    public LivingEntity getTarget() {
        return target;
    }
}
