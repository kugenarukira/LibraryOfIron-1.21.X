package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

public final class StaminaCap implements LoICapability<StaminaCap> {

    private final float maxStamina;
    private float currentStamina;

    public StaminaCap(float maxStamina) {
        this.maxStamina = maxStamina;
        this.currentStamina = maxStamina;
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {

    }

    @Override
    public void tick(LoIContext context, Object holder) {
        currentStamina = Math.min(maxStamina, currentStamina + 0.5f); // regen per tick
    }

    public boolean consume(float amount) {
        if (currentStamina < amount) return false;
        currentStamina -= amount;
        return true;
    }

    @Override
    public StaminaCap getData() {
        return this;
    }

    public float getCurrentStamina() {
        return currentStamina;
    }
}