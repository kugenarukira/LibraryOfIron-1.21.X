package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.ArrayList;
import java.util.List;

public final class UMRSkeletonDef {

    private final List<UMRBoneDef> bones;

    public UMRSkeletonDef(List<UMRBoneDef> bones) {
        this.bones = bones != null ? new ArrayList<>(bones) : new ArrayList<>();
    }

    public List<UMRBoneDef> bones() {
        return new ArrayList<>(bones);
    }

    public int boneCount() {
        return bones.size();
    }

    public boolean isEmpty() {
        return bones.isEmpty();
    }
}