package net.ironedge.libraryofiron.render.umr.skeleton;

import org.joml.Matrix4f;

public final class Bone {

    private final String name;
    private final int parentIndex;

    private final Matrix4f bindPose;
    private final Matrix4f inverseBindPose;

    public Bone(
            String name,
            int parentIndex,
            Matrix4f bindPose,
            Matrix4f inverseBindPose
    ) {
        this.name = name;
        this.parentIndex = parentIndex;
        this.bindPose = new Matrix4f(bindPose);
        this.inverseBindPose = new Matrix4f(inverseBindPose);
    }

    public String name() {
        return name;
    }

    public int parentIndex() {
        return parentIndex;
    }

    public Matrix4f bindPose() {
        return new Matrix4f(bindPose);
    }

    public Matrix4f inverseBindPose() {
        return new Matrix4f(inverseBindPose);
    }
}