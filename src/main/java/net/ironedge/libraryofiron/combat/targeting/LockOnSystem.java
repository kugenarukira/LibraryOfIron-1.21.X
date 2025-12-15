package net.ironedge.libraryofiron.combat.targeting;

import net.minecraft.world.entity.LivingEntity;

public final class LockOnSystem {

    private LivingEntity currentTarget;

    public void lockOn(LivingEntity target) {
        this.currentTarget = target;
    }

    public void clearLock() {
        this.currentTarget = null;
    }

    public LivingEntity getTarget() {
        return currentTarget;
    }
}