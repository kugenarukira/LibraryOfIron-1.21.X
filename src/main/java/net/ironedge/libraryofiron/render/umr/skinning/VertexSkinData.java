package net.ironedge.libraryofiron.render.umr.skinning;

public final class VertexSkinData {

    private final VertexWeight[] weights;

    public VertexSkinData(VertexWeight... weights) {
        this.weights = weights;
    }

    public VertexWeight[] weights() {
        return weights;
    }
}