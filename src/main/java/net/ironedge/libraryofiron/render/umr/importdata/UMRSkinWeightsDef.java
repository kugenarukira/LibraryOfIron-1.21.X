package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.ArrayList;
import java.util.List;

public final class UMRSkinWeightsDef {

    private final List<UMRVertexSkinDataDef> vertices;

    public UMRSkinWeightsDef(List<UMRVertexSkinDataDef> vertices) {
        this.vertices = vertices != null ? new ArrayList<>(vertices) : new ArrayList<>();
    }

    public List<UMRVertexSkinDataDef> vertices() {
        return new ArrayList<>(vertices);
    }

    public UMRVertexSkinDataDef vertex(int index) {
        if (index < 0 || index >= vertices.size()) {
            return null;
        }
        return vertices.get(index);
    }

    public int vertexCount() {
        return vertices.size();
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }
}