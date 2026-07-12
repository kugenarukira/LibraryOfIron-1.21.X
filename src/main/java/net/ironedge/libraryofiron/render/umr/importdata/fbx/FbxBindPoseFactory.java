package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose;
import org.joml.Matrix4f;

public final class FbxBindPoseFactory {

    private FbxBindPoseFactory() {}

    public static SkeletonPose createBindPose(Skeleton skeleton) {
        if (skeleton == null) {
            return null;
        }

        SkeletonPose pose = new SkeletonPose(skeleton.boneCount());

        for (int i = 0; i < skeleton.boneCount(); i++) {
            pose.setTransform(i, skeleton.bone(i).bindPose());
        }

        return pose;
    }

    public static SkeletonPose createIdentityPose(Skeleton skeleton) {
        if (skeleton == null) {
            return null;
        }

        SkeletonPose pose = new SkeletonPose(skeleton.boneCount());

        for (int i = 0; i < skeleton.boneCount(); i++) {
            pose.setTransform(i, new Matrix4f().identity());
        }

        return pose;
    }
}