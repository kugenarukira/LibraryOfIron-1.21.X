package net.ironedge.libraryofiron.render.pose;

public final class PoseGraph {
    private static final PoseGraph INSTANCE = new PoseGraph();

    public static PoseGraph get() {
        return INSTANCE;
    }

    private final PoseFrame frame = new PoseFrame();

    private PoseGraph() {}

    public PoseFrame frame() {
        return frame;
    }

    public void beginFrame() {
        frame.clear();
    }
}
