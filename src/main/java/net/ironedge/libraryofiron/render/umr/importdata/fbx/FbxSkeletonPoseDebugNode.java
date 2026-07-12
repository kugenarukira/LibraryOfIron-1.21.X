package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.skeleton.Bone;
import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public final class FbxSkeletonPoseDebugNode extends RenderNode {

    private final MeshInstance instance;
    private final int boneIndex;
    private final float bendDegrees;

    public FbxSkeletonPoseDebugNode(
            MeshInstance instance,
            int boneIndex,
            float bendDegrees
    ) {
        super(RenderPhase.DEBUG);
        this.instance = instance;
        this.boneIndex = boneIndex;
        this.bendDegrees = bendDegrees;
    }

    @Override
    public void render(FrameContext frame) {
        if (instance == null) return;
        if (instance.asset() == null || !instance.asset().hasSkeleton()) return;

        Skeleton skeleton = instance.asset().skeleton();

        if (boneIndex < 0 || boneIndex >= skeleton.boneCount()) {
            return;
        }

        SkeletonPose pose = instance.skeletonPose();

        if (pose == null) {
            pose = FbxBindPoseFactory.createBindPose(skeleton);
            instance.skeletonPose(pose);
        }

        resetToBindPose(skeleton, pose);

        Minecraft mc = Minecraft.getInstance();

        float age = 0f;
        if (mc != null && mc.level != null) {
            age = mc.level.getGameTime() + frame.partialTicks();
        }

        float angle = (float) Math.sin(age * 0.08f) * (float) Math.toRadians(bendDegrees);

        Bone bone = skeleton.bone(boneIndex);
        int parentIndex = bone.parentIndex();

        Matrix4f bind = bone.bindPose();

        Matrix4f animatedGlobal;

        if (parentIndex >= 0 && parentIndex < skeleton.boneCount()) {
            Matrix4f parentBind = skeleton.bone(parentIndex).bindPose();

            Matrix4f localFromParent = new Matrix4f(parentBind)
                    .invert()
                    .mul(bind);

            animatedGlobal = new Matrix4f(parentBind)
                    .rotateZ(angle)
                    .mul(localFromParent);
        } else {
            animatedGlobal = new Matrix4f(bind)
                    .rotateZ(angle);
        }

        pose.setTransform(boneIndex, animatedGlobal);
    }

    private static void resetToBindPose(
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        for (int i = 0; i < skeleton.boneCount(); i++) {
            pose.setTransform(i, skeleton.bone(i).bindPose());
        }
    }
}