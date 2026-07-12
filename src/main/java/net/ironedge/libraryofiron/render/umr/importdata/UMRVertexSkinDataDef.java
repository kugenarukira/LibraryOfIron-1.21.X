package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.ArrayList;
import java.util.List;

public final class UMRVertexSkinDataDef {

    private final List<UMRVertexWeightDef> weights;

    public UMRVertexSkinDataDef(List<UMRVertexWeightDef> weights) {
        this.weights = weights != null ? new ArrayList<>(weights) : new ArrayList<>();
    }

    public List<UMRVertexWeightDef> weights() {
        return new ArrayList<>(weights);
    }

    public int weightCount() {
        return weights.size();
    }
}