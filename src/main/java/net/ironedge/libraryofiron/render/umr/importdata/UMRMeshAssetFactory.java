package net.ironedge.libraryofiron.render.umr.importdata;

import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.morph.MorphTarget;
import net.ironedge.libraryofiron.render.umr.skeleton.Bone;
import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skinning.MeshSkinWeights;
import net.ironedge.libraryofiron.render.umr.skinning.VertexSkinData;
import net.ironedge.libraryofiron.render.umr.skinning.VertexWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UMRMeshAssetFactory {

    private UMRMeshAssetFactory() {}

    public static MeshAsset build(UMRMeshAssetDef def) {
        if (def == null) {
            throw new IllegalArgumentException("def must not be null");
        }

        List<MeshSurface> surfaces = new ArrayList<>();

        for (UMRMeshSurfaceDef s : def.surfaces()) {
            MeshSurface surface = new MeshSurface(
                    s.name(),
                    s.positions(),
                    s.normals(),
                    s.uvs(),
                    s.indices(),
                    s.materialSlot()
            );

            surface.skinWeights(buildSkinWeights(s.skinWeights()));
            surfaces.add(surface);
        }

        Map<String, MorphTarget> morphTargets = new HashMap<>();

        for (var e : def.morphTargets().entrySet()) {
            UMRMorphTargetDef mt = e.getValue();

            morphTargets.put(
                    mt.name(),
                    new MorphTarget(
                            mt.name(),
                            mt.positionDeltasBySurface()
                    )
            );
        }

        Skeleton skeleton = buildSkeleton(def.skeleton());

        return new MeshAsset(
                def.id(),
                surfaces,
                morphTargets,
                skeleton
        );
    }

    private static Skeleton buildSkeleton(UMRSkeletonDef def) {
        if (def == null || def.isEmpty()) {
            return null;
        }

        List<Bone> bones = new ArrayList<>();

        for (UMRBoneDef b : def.bones()) {
            bones.add(new Bone(
                    b.name(),
                    b.parentIndex(),
                    b.bindPoseMatrix(),
                    b.inverseBindPoseMatrix()
            ));
        }

        return new Skeleton(bones);
    }

    private static MeshSkinWeights buildSkinWeights(UMRSkinWeightsDef def) {
        if (def == null || def.isEmpty()) {
            return null;
        }

        List<VertexSkinData> vertices = new ArrayList<>();

        for (int vi = 0; vi < def.vertexCount(); vi++) {
            UMRVertexSkinDataDef src = def.vertex(vi);

            if (src == null || src.weightCount() == 0) {
                vertices.add(new VertexSkinData());
                continue;
            }

            List<UMRVertexWeightDef> srcWeights = src.weights();
            VertexWeight[] bakedWeights = new VertexWeight[srcWeights.size()];

            for (int wi = 0; wi < srcWeights.size(); wi++) {
                UMRVertexWeightDef w = srcWeights.get(wi);

                bakedWeights[wi] = new VertexWeight(
                        w.boneIndex(),
                        w.weight()
                );
            }

            vertices.add(new VertexSkinData(bakedWeights));
        }

        return new MeshSkinWeights(vertices);
    }
}