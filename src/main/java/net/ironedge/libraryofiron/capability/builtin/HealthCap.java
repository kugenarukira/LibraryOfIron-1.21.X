package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

public final class HealthCap implements LoICapability<HealthCap> {

    private final float maxHealth;
    private float currentHealth;

    public HealthCap(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {
        // optionally fire attach event
    }

    @Override
    public void tick(LoIContext context, Object holder) {
        // regen or passive effects if needed
    }

    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public HealthCap getData() {
        return this;
    }
}