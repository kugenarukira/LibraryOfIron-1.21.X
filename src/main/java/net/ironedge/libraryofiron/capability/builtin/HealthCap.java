package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

public final class HealthCap implements LoICapability<HealthCap> {

    private float maxHealth;
    private float currentHealth;
    private boolean downed;

    public HealthCap(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.downed = false;
    }

    public HealthCap() {
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {}

    @Override
    public void tick(LoIContext context, Object holder) {
        // Optional: health regen logic
        if (!downed && currentHealth < maxHealth) {
            currentHealth = Math.min(maxHealth, currentHealth + 0.5f);
        }
    }

    public void damage(float amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            downed = true;
            currentHealth = 0;
        }
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
        if (currentHealth > 0) downed = false;
    }

    public boolean isDowned() {
        return downed;
    }

    public boolean isDead() {
        return downed && currentHealth <= 0;
    }

    @Override
    public HealthCap getData() {
        return this;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }
}
