package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.List;
import java.util.Map;

public final class UMRMeshJsonModels {

    private UMRMeshJsonModels() {}

    public static final class MeshAssetJson {
        public String id;
        public List<MeshSurfaceJson> surfaces;
        public Map<String, MorphTargetJson> morphTargets;
        public SkeletonJson skeleton;
    }

    public static final class MeshSurfaceJson {
        public String name;
        public float[] positions;
        public float[] normals;
        public float[] uvs;
        public int[] indices;
        public String materialSlot;
        public List<VertexSkinDataJson> skinWeights;
    }

    public static final class MorphTargetJson {
        public String name;
        public Map<String, float[]> positionDeltasBySurface;
    }

    public static final class SkeletonJson {
        public List<BoneJson> bones;
    }

    public static final class BoneJson {
        public String name;
        public int index;
        public int parentIndex = -1;
        public float[] bindPose;
        public float[] inverseBindPose;
    }

    public static final class VertexSkinDataJson {
        public List<VertexWeightJson> weights;
    }

    public static final class VertexWeightJson {
        public int boneIndex;
        public float weight;
    }
}