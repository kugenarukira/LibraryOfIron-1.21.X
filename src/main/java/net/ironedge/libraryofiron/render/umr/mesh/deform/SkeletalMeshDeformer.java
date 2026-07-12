package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.skinning.MeshSkinner;

public final class SkeletalMeshDeformer {

    private SkeletalMeshDeformer() {}

    public static float[] deformPositions(
            MeshInstance instance,
            MeshSurface surface,
            float[] basePositions
    ) {
        if (!canSkin(instance, surface)) return basePositions;

        return MeshSkinner.skinPositions(
                basePositions,
                surface.skinWeights(),
                instance.asset().skeleton(),
                instance.skeletonPose()
        );
    }

    public static float[] deformNormals(
            MeshInstance instance,
            MeshSurface surface,
            float[] baseNormals
    ) {
        if (!canSkin(instance, surface)) return baseNormals;



        return MeshSkinner.skinNormals(
                baseNormals,
                surface.skinWeights(),
                instance.asset().skeleton(),
                instance.skeletonPose()
        );
    }

    private static boolean canSkin(
            MeshInstance instance,
            MeshSurface surface
    ) {
        return instance != null
                && surface != null
                && instance.asset() != null
                && instance.asset().skeleton() != null
                && instance.skeletonPose() != null
                && surface.skinWeights() != null;
    }
}