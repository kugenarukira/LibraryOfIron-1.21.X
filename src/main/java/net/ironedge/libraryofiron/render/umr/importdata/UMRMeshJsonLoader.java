package net.ironedge.libraryofiron.render.umr.importdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ironedge.libraryofiron.render.umr.importdata.UMRMeshJsonModels.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UMRMeshJsonLoader {

    private static final Gson GSON = new GsonBuilder().create();

    private UMRMeshJsonLoader() {}

    public static UMRMeshAssetDef loadDef(Reader reader) {
        MeshAssetJson json = GSON.fromJson(reader, MeshAssetJson.class);
        if (json == null) {
            throw new IllegalArgumentException("Failed to parse mesh json");
        }

        List<UMRMeshSurfaceDef> surfaces = new ArrayList<>();
        if (json.surfaces != null) {
            for (MeshSurfaceJson s : json.surfaces) {
                surfaces.add(new UMRMeshSurfaceDef(
                        s.name,
                        s.positions,
                        s.normals,
                        s.uvs,
                        s.indices,
                        s.materialSlot,
                        readSkinWeights(s.skinWeights)
                ));
            }
        }

        Map<String, UMRMorphTargetDef> morphs = new HashMap<>();
        if (json.morphTargets != null) {
            for (var e : json.morphTargets.entrySet()) {
                MorphTargetJson mt = e.getValue();
                morphs.put(e.getKey(), new UMRMorphTargetDef(
                        mt.name != null ? mt.name : e.getKey(),
                        mt.positionDeltasBySurface
                ));
            }
        }

        UMRSkeletonDef skeleton = readSkeleton(json.skeleton);

        return new UMRMeshAssetDef(
                json.id,
                surfaces,
                morphs,
                skeleton
        );
    }

    private static UMRSkeletonDef readSkeleton(SkeletonJson json) {
        if (json == null || json.bones == null || json.bones.isEmpty()) {
            return null;
        }

        List<UMRBoneDef> bones = new ArrayList<>();

        for (BoneJson b : json.bones) {
            bones.add(new UMRBoneDef(
                    b.name,
                    b.index,
                    b.parentIndex,
                    b.bindPose,
                    b.inverseBindPose
            ));
        }

        return new UMRSkeletonDef(bones);
    }

    private static UMRSkinWeightsDef readSkinWeights(List<VertexSkinDataJson> json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        List<UMRVertexSkinDataDef> vertices = new ArrayList<>();

        for (VertexSkinDataJson vertexJson : json) {
            List<UMRVertexWeightDef> weights = new ArrayList<>();

            if (vertexJson != null && vertexJson.weights != null) {
                for (VertexWeightJson w : vertexJson.weights) {
                    weights.add(new UMRVertexWeightDef(
                            w.boneIndex,
                            w.weight
                    ));
                }
            }

            vertices.add(new UMRVertexSkinDataDef(weights));
        }

        return new UMRSkinWeightsDef(vertices);
    }
}