package net.ironedge.libraryofiron.render.umr.importdata;

import org.joml.Matrix4f;

import java.util.Arrays;

public final class UMRBoneDef {

    private final String name;
    private final int index;
    private final int parentIndex;

    /**
     * 16 floats, column-major JOML Matrix4f layout.
     */
    private final float[] bindPose;

    /**
     * 16 floats, column-major JOML Matrix4f layout.
     */
    private final float[] inverseBindPose;

    public UMRBoneDef(
            String name,
            int index,
            int parentIndex,
            float[] bindPose,
            float[] inverseBindPose
    ) {
        this.name = name;
        this.index = index;
        this.parentIndex = parentIndex;
        this.bindPose = bindPose != null ? Arrays.copyOf(bindPose, bindPose.length) : identityArray();
        this.inverseBindPose = inverseBindPose != null ? Arrays.copyOf(inverseBindPose, inverseBindPose.length) : identityArray();

        if (this.bindPose.length != 16) {
            throw new IllegalArgumentException("bindPose must have 16 floats for bone " + name);
        }
        if (this.inverseBindPose.length != 16) {
            throw new IllegalArgumentException("inverseBindPose must have 16 floats for bone " + name);
        }
    }

    public String name() {
        return name;
    }

    public int index() {
        return index;
    }

    public int parentIndex() {
        return parentIndex;
    }

    public float[] bindPoseArray() {
        return Arrays.copyOf(bindPose, bindPose.length);
    }

    public float[] inverseBindPoseArray() {
        return Arrays.copyOf(inverseBindPose, inverseBindPose.length);
    }

    public Matrix4f bindPoseMatrix() {
        return new Matrix4f().set(bindPose);
    }

    public Matrix4f inverseBindPoseMatrix() {
        return new Matrix4f().set(inverseBindPose);
    }

    private static float[] identityArray() {
        float[] out = new float[16];
        new Matrix4f().identity().get(out);
        return out;
    }
}