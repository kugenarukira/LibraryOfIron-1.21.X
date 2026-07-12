package net.ironedge.libraryofiron.render.umar.material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class UMaterialPassPlan {

    private final List<UMaterialRenderData> passes;

    private UMaterialPassPlan(List<UMaterialRenderData> passes) {
        List<UMaterialRenderData> sorted = new ArrayList<>(passes);
        sorted.sort(Comparator.comparingInt(UMaterialRenderData::priority));
        this.passes = List.copyOf(sorted);
    }

    public List<UMaterialRenderData> passes() {
        return passes;
    }

    public boolean isEmpty() {
        return passes.isEmpty();
    }

    public int size() {
        return passes.size();
    }

    public UMaterialRenderData first() {
        return passes.isEmpty() ? null : passes.get(0);
    }

    public static UMaterialPassPlan of(List<UMaterialRenderData> passes) {
        return new UMaterialPassPlan(passes);
    }
}