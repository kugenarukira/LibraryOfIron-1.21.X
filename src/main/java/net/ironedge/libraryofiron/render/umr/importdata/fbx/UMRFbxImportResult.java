package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import net.ironedge.libraryofiron.render.umar.material.UMaterialInstance;
import net.ironedge.libraryofiron.render.umr.importdata.UMRImportedMaterialDef;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;

import java.util.HashMap;
import java.util.Map;

public final class UMRFbxImportResult {

    private final MeshAsset asset;
    private final Map<String, UMRImportedMaterialDef> importedMaterials;
    private final Map<String, UMaterialInstance> materialInstances;

    public UMRFbxImportResult(
            MeshAsset asset,
            Map<String, UMRImportedMaterialDef> importedMaterials,
            Map<String, UMaterialInstance> materialInstances
    ) {
        this.asset = asset;
        this.importedMaterials = importedMaterials != null ? new HashMap<>(importedMaterials) : new HashMap<>();
        this.materialInstances = materialInstances != null ? new HashMap<>(materialInstances) : new HashMap<>();
    }

    public MeshAsset asset() {
        return asset;
    }

    public Map<String, UMRImportedMaterialDef> importedMaterials() {
        return new HashMap<>(importedMaterials);
    }

    public Map<String, UMaterialInstance> materialInstances() {
        return new HashMap<>(materialInstances);
    }
}