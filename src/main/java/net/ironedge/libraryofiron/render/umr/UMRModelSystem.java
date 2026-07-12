package net.ironedge.libraryofiron.render.umr;

import net.ironedge.libraryofiron.render.pose.PoseGraph;

import java.util.ArrayList;
import java.util.List;

public final class UMRModelSystem {

    private static final UMRModelSystem INSTANCE = new UMRModelSystem();

    public static UMRModelSystem get() {
        return INSTANCE;
    }

    private final List<UMRModelInstance> instances = new ArrayList<>();

    private UMRModelSystem() {}

    public void addInstance(UMRModelInstance inst) {
        instances.add(inst);
    }

    public List<UMRModelInstance> instances() {
        return instances;
    }

    public void publishAll(PoseGraph graph) {
        for (UMRModelInstance inst : instances) {
            inst.publish(graph);
        }
    }
}
