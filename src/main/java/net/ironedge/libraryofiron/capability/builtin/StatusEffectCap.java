package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

import java.util.ArrayList;
import java.util.List;

public final class StatusEffectCap implements LoICapability<StatusEffectCap> {

    private final List<StatusEffect> effects = new ArrayList<>();

    @Override
    public void onAttach(LoIContext context, Object holder) {

    }

    @Override
    public void tick(LoIContext context, Object holder) {
        effects.removeIf(effect -> {
            effect.applyEffect(holder);
            return effect.tick();
        });
    }

    public void addEffect(StatusEffect effect) {
        effects.add(effect);
    }

    public List<StatusEffect> getEffects() {
        return List.copyOf(effects);
    }

    @Override
    public StatusEffectCap getData() {
        return this;
    }

    public static abstract class StatusEffect {
        protected int duration; // ticks

        public StatusEffect(int duration) {
            this.duration = duration;
        }

        public boolean tick() {
            duration--;
            return duration <= 0;
        }

        public abstract void applyEffect(Object holder);
    }
}