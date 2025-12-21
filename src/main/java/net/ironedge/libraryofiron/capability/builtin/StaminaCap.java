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
        currentStamina = Math.min(maxStamina, currentStamina + 0.5f);
    }

    /** Can the entity afford this stamina cost? */
    public boolean canAfford(float amount) {
        return currentStamina >= amount;
    }

    /**
     * Attempts to consume stamina.
     * @return true if successful
     */
    public boolean tryConsume(float amount) {
        if (!canAfford(amount)) return false;
        currentStamina -= amount;
        return true;
    }
    public void add(float amount) {
        this.currentStamina = Math.min(maxStamina, this.currentStamina + amount);
    }

    /**
     * Forcefully drains stamina (used by effects, penalties, etc.)
     */
    public void drain(float amount) {
        currentStamina = Math.max(0, currentStamina - amount);
    }

    @Override
    public StaminaCap getData() {
        return this;
    }

    public float getCurrentStamina() {
        return currentStamina;
    }
}
