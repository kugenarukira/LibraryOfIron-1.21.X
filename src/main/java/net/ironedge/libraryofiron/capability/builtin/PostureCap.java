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

    }


    @Override
    public void onAttach(LoIContext context, Object holder) {

    }

    @Override
    public void tick(LoIContext context, Object holder) {
        if (!staggered && currentPosture < maxPosture) {
            currentPosture = Math.min(maxPosture, currentPosture + 0.5f); // regen
        }
    }

    public void takeDamage(float amount) {
        currentPosture -= amount;
        if (currentPosture <= 0) {
            staggered = true;
            currentPosture = 0;
        }
    }

    public void resetPosture() {
        staggered = false;
        currentPosture = maxPosture;
    }

    public boolean isStaggered() {
        return staggered;
    }

    @Override
    public PostureCap getData() {
        return this;
    }

    public float getCurrentPosture() {
        return currentPosture;
    }
}