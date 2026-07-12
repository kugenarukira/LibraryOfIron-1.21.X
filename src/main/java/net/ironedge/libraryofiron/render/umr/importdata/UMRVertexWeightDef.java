package net.ironedge.libraryofiron.render.umr.importdata;

public final class UMRVertexWeightDef {

    private final int boneIndex;
    private final float weight;

    public UMRVertexWeightDef(int boneIndex, float weight) {
        this.boneIndex = boneIndex;
        this.weight = weight;
    }

    public int boneIndex() {
        return boneIndex;
    }

    public float weight() {
        return weight;
    }
}