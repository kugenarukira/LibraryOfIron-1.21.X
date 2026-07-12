package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseGraph;

import java.util.ArrayList;
import java.util.List;

public final class BridgeSystem {

    private static final BridgeSystem INSTANCE = new BridgeSystem();
    public static BridgeSystem get() { return INSTANCE; }

    private final List<UMRBridgeInstance> bridges = new ArrayList<>();

    private BridgeSystem() {}

    public void add(UMRBridgeInstance bridge) {
        bridges.add(bridge);
    }

    public List<UMRBridgeInstance> bridges() {
        return bridges;
    }

    public void solveAll(FrameContext frame, PoseGraph graph) {
        for (UMRBridgeInstance b : bridges) {
            b.solveAndPublish(frame, graph);
        }
    }
}
