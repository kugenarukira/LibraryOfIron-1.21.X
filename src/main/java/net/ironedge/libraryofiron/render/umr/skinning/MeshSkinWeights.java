package net.ironedge.libraryofiron.render.umr.skinning;

import java.util.List;

public final class MeshSkinWeights {

    private final List<VertexSkinData> vertices;

    public MeshSkinWeights(List<VertexSkinData> vertices) {
        this.vertices = List.copyOf(vertices);
    }

    public VertexSkinData vertex(int index) {
        return vertices.get(index);
    }

    public int vertexCount() {
        return vertices.size();
    }
}