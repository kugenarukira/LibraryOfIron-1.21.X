package net.ironedge.libraryofiron.render.core;

public abstract class RenderNode {

    private final RenderPhase phase;
    private boolean enabled = true;

    protected RenderNode(RenderPhase phase) {
        this.phase = phase;
    }

    public RenderPhase phase() {
        return phase;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Called every rendered frame */
    public abstract void render(FrameContext context);
}
