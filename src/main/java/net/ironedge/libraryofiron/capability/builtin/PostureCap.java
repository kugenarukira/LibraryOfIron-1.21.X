package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

public final class PostureCap implements LoICapability<PostureCap> {

    private float maxPosture;
    private float currentPosture;
    private boolean staggered;

    public PostureCap(float maxPosture) {
        this.maxPosture = maxPosture;
        this.currentPosture = maxPosture;
        this.staggered = false;
    }

    public PostureCap() {
        this(100f);
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {}

    @Override
    public void tick(LoIContext context, Object holder) {
        if (!staggered && currentPosture < maxPosture) {
            currentPosture = Math.min(maxPosture, currentPosture + 0.5f);
        }
    }

    public void takeDamage(float amount) {
        currentPosture -= amount;
        if (currentPosture <= 0) {
            staggered = true;
            currentPosture = 0;
        }
    }

    public void breakPosture() {
        currentPosture = 0;
        staggered = true;
    }

    public void resetPosture() {
        currentPosture = maxPosture;
        staggered = false;
    }

    public boolean isStaggered() {
        return staggered;
    }

    /** New method to match other systems */
    public boolean isBroken() {
        return staggered; // same as isStaggered
    }

    public float getCurrentPosture() {
        return currentPosture;
    }

    @Override
    public PostureCap getData() {
        return this;
    }
}
