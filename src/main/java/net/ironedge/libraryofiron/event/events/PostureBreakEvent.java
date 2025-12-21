package net.ironedge.libraryofiron.event.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class PostureBreakEvent extends LoIEvent {
    private final LivingEntity target;
    public PostureBreakEvent(LivingEntity target) { this.target = target; }
    public LivingEntity getTarget() { return target; }
}