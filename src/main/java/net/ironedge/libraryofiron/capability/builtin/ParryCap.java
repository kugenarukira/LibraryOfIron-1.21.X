package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

public final class ParryCap implements LoICapability<ParryCap> {

    private int parryTicksRemaining;
    private boolean successfulParry;

    public void openParryWindow(int ticks) {
        this.parryTicksRemaining = ticks;
        this.successfulParry = false;
    }

    public boolean isParryActive() {
        return parryTicksRemaining > 0;
    }

    public boolean consumeParry() {
        if (!isParryActive()) return false;
        successfulParry = true;
        parryTicksRemaining = 0;
        return true;
    }

    public boolean wasSuccessful() {
        return successfulParry;
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {

    }

    @Override
    public void tick(LoIContext context, Object holder) {
        if (parryTicksRemaining > 0) {
            parryTicksRemaining--;
        }
    }

    @Override
    public ParryCap getData() {
        return this;
    }
}
