package net.ironedge.libraryofiron.render.core;

public final class RenderEngine {

    private final RenderGraph graph = new RenderGraph();

    public RenderGraph graph() {
        return graph;
    }

    /** Called once per rendered frame */
    public void renderFrame(FrameContext context) {
        graph.render(context);
    }
}
