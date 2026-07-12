package net.ironedge.libraryofiron.render.umr.skeleton;

import org.joml.Matrix4f;

public final class SkeletonPose {

    private final Matrix4f[] transforms;

    public SkeletonPose(int boneCount) {
        this.transforms = new Matrix4f[Math.max(0, boneCount)];

        for (int i = 0; i < transforms.length; i++) {
            transforms[i] = new Matrix4f().identity();
        }
    }

    public Matrix4f transform(int index) {
        if (index < 0 || index >= transforms.length) {
            return new Matrix4f().identity();
        }

        return transforms[index];
    }

    public void setTransform(int index, Matrix4f transform) {
        if (index < 0 || index >= transforms.length || transform == null) {
            return;
        }

        transforms[index].set(transform);
    }

    public int boneCount() {
        return transforms.length;
    }
}