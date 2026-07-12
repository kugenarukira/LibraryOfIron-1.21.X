package net.ironedge.libraryofiron.render.core;

import net.ironedge.libraryofiron.render.model.render.ModelRenderNode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RenderGraph {

    private final Map<RenderPhase, List<RenderNode>> nodes = new EnumMap<>(RenderPhase.class);

    public RenderGraph() {
        for (RenderPhase phase : RenderPhase.values()) {
            nodes.put(phase, new ArrayList<>());
        }
    }

    public void addNode(RenderNode node) {
        nodes.get(node.phase()).add(node);
    }

    public void render(FrameContext context) {
        for (RenderPhase phase : RenderPhase.values()) {
            for (RenderNode node : nodes.get(phase)) {
                if (node.isEnabled()) {
                    node.render(context);
                }
            }
        }
    }
}
