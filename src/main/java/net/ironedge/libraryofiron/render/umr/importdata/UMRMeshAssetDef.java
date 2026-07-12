package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UMRMeshAssetDef {

    private final String id;
    private final List<UMRMeshSurfaceDef> surfaces;
    private final Map<String, UMRMorphTargetDef> morphTargets;
    private final UMRSkeletonDef skeleton;
    private final Map<String, UMRImportedMaterialDef> materials;

    public UMRMeshAssetDef(
            String id,
            List<UMRMeshSurfaceDef> surfaces,
            Map<String, UMRMorphTargetDef> morphTargets
    ) {
        this(id, surfaces, morphTargets, null, Map.of());
    }

    public UMRMeshAssetDef(
            String id,
            List<UMRMeshSurfaceDef> surfaces,
            Map<String, UMRMorphTargetDef> morphTargets,
            UMRSkeletonDef skeleton
    ) {
        this(id, surfaces, morphTargets, skeleton, Map.of());
    }

    public UMRMeshAssetDef(
            String id,
            List<UMRMeshSurfaceDef> surfaces,
            Map<String, UMRMorphTargetDef> morphTargets,
            UMRSkeletonDef skeleton,
            Map<String, UMRImportedMaterialDef> materials
    ) {
        this.id = id;
        this.surfaces = surfaces != null ? new ArrayList<>(surfaces) : new ArrayList<>();
        this.morphTargets = morphTargets != null ? new HashMap<>(morphTargets) : new HashMap<>();
        this.skeleton = skeleton;
        this.materials = materials != null ? new HashMap<>(materials) : new HashMap<>();
    }

    public String id() {
        return id;
    }

    public List<UMRMeshSurfaceDef> surfaces() {
        return new ArrayList<>(surfaces);
    }

    public Map<String, UMRMorphTargetDef> morphTargets() {
        return new HashMap<>(morphTargets);
    }

    public UMRSkeletonDef skeleton() {
        return skeleton;
    }

    public boolean hasSkeleton() {
        return skeleton != null && !skeleton.isEmpty();
    }

    public Map<String, UMRImportedMaterialDef> materials() {
        return new HashMap<>(materials);
    }

    public boolean hasMaterials() {
        return !materials.isEmpty();
    }
}