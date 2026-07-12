package net.ironedge.libraryofiron.render.umr.mesh;

import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose;
import net.ironedge.libraryofiron.render.umr.skinning.MeshSkinWeights;

import java.util.Arrays;

public final class MeshSurface {

    private final String name;
    private final float[] positions; // xyz xyz xyz
    private final float[] normals;   // xyz xyz xyz
    private final float[] uvs;       // uv uv uv
    private final int[] indices;
    private final String materialSlot;
    private MeshSkinWeights skinWeights;
    public float[] positionsUnsafe() { return positions; }
    public float[] normalsUnsafe() { return normals; }
    public float[] uvsUnsafe() { return uvs; }
    public int[] indicesUnsafe() { return indices; }

    public MeshSurface(
            String name,
            float[] positions,
            float[] normals,
            float[] uvs,
            int[] indices,

            String materialSlot
    ) {
        if (positions == null || positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions must be xyz-packed");
        }
        if (normals != null && normals.length != positions.length) {
            throw new IllegalArgumentException("normals must match positions length");
        }
        if (uvs != null && uvs.length / 2 != positions.length / 3) {
            throw new IllegalArgumentException("uv count must match vertex count");
        }
        if (indices == null || indices.length == 0) {
            throw new IllegalArgumentException("indices must not be empty");
        }

        this.name = name;
        this.positions = Arrays.copyOf(positions, positions.length);
        this.normals = normals != null ? Arrays.copyOf(normals, normals.length) : null;
        this.uvs = uvs != null ? Arrays.copyOf(uvs, uvs.length) : null;
        this.indices = Arrays.copyOf(indices, indices.length);
        this.materialSlot = materialSlot;
    }


    public net.ironedge.libraryofiron.render.umr.skinning.MeshSkinWeights skinWeights() {
        return skinWeights;
    }

    public MeshSurface skinWeights(net.ironedge.libraryofiron.render.umr.skinning.MeshSkinWeights skinWeights) {
        this.skinWeights = skinWeights;
        return this;
    }

    public boolean hasSkinWeights() {
        return skinWeights != null;
    }

    public String name() {
        return name;
    }

    public float[] positions() {
        return Arrays.copyOf(positions, positions.length);
    }

    public float[] normals() {
        return normals != null ? Arrays.copyOf(normals, normals.length) : null;
    }

    public float[] uvs() {
        return uvs != null ? Arrays.copyOf(uvs, uvs.length) : null;
    }

    public int[] indices() {
        return Arrays.copyOf(indices, indices.length);
    }

    public String materialSlot() {
        return materialSlot;
    }

    public int vertexCount() {
        return positions.length / 3;
    }

    public int indexCount() {
        return indices.length;
    }

    public MeshBounds bounds() {
        return MeshBounds.fromPositions(positions);
    }

}