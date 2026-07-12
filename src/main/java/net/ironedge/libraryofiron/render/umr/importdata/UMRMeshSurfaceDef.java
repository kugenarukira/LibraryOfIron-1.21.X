package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.Arrays;

public final class UMRMeshSurfaceDef {

    private final String name;
    private final float[] positions;
    private final float[] normals;
    private final float[] uvs;
    private final int[] indices;
    private final String materialSlot;
    private final UMRSkinWeightsDef skinWeights;

    public UMRMeshSurfaceDef(
            String name,
            float[] positions,
            float[] normals,
            float[] uvs,
            int[] indices,
            String materialSlot
    ) {
        this(name, positions, normals, uvs, indices, materialSlot, null);
    }

    public UMRMeshSurfaceDef(
            String name,
            float[] positions,
            float[] normals,
            float[] uvs,
            int[] indices,
            String materialSlot,
            UMRSkinWeightsDef skinWeights
    ) {
        this.name = name;
        this.positions = positions != null ? Arrays.copyOf(positions, positions.length) : null;
        this.normals = normals != null ? Arrays.copyOf(normals, normals.length) : null;
        this.uvs = uvs != null ? Arrays.copyOf(uvs, uvs.length) : null;
        this.indices = indices != null ? Arrays.copyOf(indices, indices.length) : null;
        this.materialSlot = materialSlot;
        this.skinWeights = skinWeights;
    }

    public String name() {
        return name;
    }

    public float[] positions() {
        return positions != null ? Arrays.copyOf(positions, positions.length) : null;
    }

    public float[] normals() {
        return normals != null ? Arrays.copyOf(normals, normals.length) : null;
    }

    public float[] uvs() {
        return uvs != null ? Arrays.copyOf(uvs, uvs.length) : null;
    }

    public int[] indices() {
        return indices != null ? Arrays.copyOf(indices, indices.length) : null;
    }

    public String materialSlot() {
        return materialSlot;
    }

    public UMRSkinWeightsDef skinWeights() {
        return skinWeights;
    }

    public boolean hasSkinWeights() {
        return skinWeights != null && !skinWeights.isEmpty();
    }
}