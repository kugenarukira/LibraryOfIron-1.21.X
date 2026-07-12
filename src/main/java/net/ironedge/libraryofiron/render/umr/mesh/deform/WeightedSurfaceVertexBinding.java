package net.ironedge.libraryofiron.render.umr.mesh.deform;

public final class WeightedSurfaceVertexBinding {
    private final int row0;
    private final int col0;
    private final float u;
    private final float v;

    public WeightedSurfaceVertexBinding(int row0, int col0, float u, float v) {
        this.row0 = row0;
        this.col0 = col0;
        this.u = u;
        this.v = v;
    }

    public int row0() { return row0; }
    public int col0() { return col0; }
    public float u() { return u; }
    public float v() { return v; }
}