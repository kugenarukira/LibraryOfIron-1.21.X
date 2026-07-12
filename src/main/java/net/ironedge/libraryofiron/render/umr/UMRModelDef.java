package net.ironedge.libraryofiron.render.umr;

import java.util.LinkedHashMap;
import java.util.Map;

public final class UMRModelDef {

    private final String id;
    private final Map<String, UMRNodeDef> nodes = new LinkedHashMap<>();

    public UMRModelDef(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public void addNode(UMRNodeDef node) {
        nodes.put(node.id(), node);
    }

    public Map<String, UMRNodeDef> nodes() {
        return nodes;
    }

    public UMRNodeDef node(String id) {
        return nodes.get(id);
    }

}
