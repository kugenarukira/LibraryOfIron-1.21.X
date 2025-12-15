package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class SpecialMoveEvent extends LoIEvent {
    private final LivingEntity performer;
    private final String moveId;

    public SpecialMoveEvent(LivingEntity performer, String moveId) {
        this.performer = performer;
        this.moveId = moveId;
    }

    public LivingEntity getPerformer() { return performer; }
    public String getMoveId() { return moveId; }
}