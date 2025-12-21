package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;
import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;

public class StatusEffectRemovedEvent extends LoIEvent {
    private final LivingEntity target;
    private final StatusEffectCap.StatusEffect effect;

    public StatusEffectRemovedEvent(LivingEntity target, StatusEffectCap.StatusEffect effect) {
        this.target = target;
        this.effect = effect;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public StatusEffectCap.StatusEffect getEffect() {
        return effect;
    }
}
