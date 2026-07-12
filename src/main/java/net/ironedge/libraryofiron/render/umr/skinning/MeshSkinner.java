package net.ironedge.libraryofiron.render.umr.skinning;

import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MeshSkinner {

    private MeshSkinner() {}

    public static float[] skinPositions(
            MeshSurface surface,
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        return skinPositions(
                surface.positionsUnsafe(),
                surface.skinWeights(),
                skeleton,
                pose
        );
    }

    public static float[] skinPositions(
            float[] basePositions,
            MeshSkinWeights weights,
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        if (basePositions == null || weights == null || skeleton == null || pose == null) {
            return basePositions;
        }

        int vertexCount = basePositions.length / 3;
        float[] out = new float[basePositions.length];

        Matrix4f[] skinMatrices = buildSkinMatrices(skeleton, pose);

        Vector3f src = new Vector3f();
        Vector3f dst = new Vector3f();

        for (int vi = 0; vi < vertexCount; vi++) {
            int p = vi * 3;

            src.set(
                    basePositions[p],
                    basePositions[p + 1],
                    basePositions[p + 2]
            );

            float ox = 0f;
            float oy = 0f;
            float oz = 0f;
            float total = 0f;

            VertexSkinData skin = weights.vertex(vi);
            if (skin == null || skin.weights() == null || skin.weights().length == 0) {
                out[p] = src.x;
                out[p + 1] = src.y;
                out[p + 2] = src.z;
                continue;
            }

            for (VertexWeight vw : skin.weights()) {
                if (vw == null || vw.weight() == 0f) continue;

                int boneIndex = vw.boneIndex();
                if (boneIndex < 0 || boneIndex >= skinMatrices.length) continue;

                dst.set(src).mulPosition(skinMatrices[boneIndex]);

                float w = vw.weight();
                ox += dst.x * w;
                oy += dst.y * w;
                oz += dst.z * w;
                total += w;
            }

            if (total <= 0f) {
                out[p] = src.x;
                out[p + 1] = src.y;
                out[p + 2] = src.z;
            } else {
                if (Math.abs(total - 1f) > 0.0001f) {
                    ox /= total;
                    oy /= total;
                    oz /= total;
                }

                out[p] = ox;
                out[p + 1] = oy;
                out[p + 2] = oz;
            }
        }

        return out;
    }

    public static float[] skinNormals(
            MeshSurface surface,
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        return skinNormals(
                surface.normalsUnsafe(),
                surface.skinWeights(),
                skeleton,
                pose
        );
    }

    public static float[] skinNormals(
            float[] baseNormals,
            MeshSkinWeights weights,
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        if (baseNormals == null || weights == null || skeleton == null || pose == null) {
            return baseNormals;
        }

        int vertexCount = baseNormals.length / 3;
        float[] out = new float[baseNormals.length];

        Matrix3f[] normalMatrices = buildNormalMatrices(skeleton, pose);

        Vector3f src = new Vector3f();
        Vector3f dst = new Vector3f();

        for (int vi = 0; vi < vertexCount; vi++) {
            int p = vi * 3;

            src.set(
                    baseNormals[p],
                    baseNormals[p + 1],
                    baseNormals[p + 2]
            );

            float ox = 0f;
            float oy = 0f;
            float oz = 0f;
            float total = 0f;

            VertexSkinData skin = weights.vertex(vi);
            if (skin == null || skin.weights() == null || skin.weights().length == 0) {
                out[p] = src.x;
                out[p + 1] = src.y;
                out[p + 2] = src.z;
                continue;
            }

            for (VertexWeight vw : skin.weights()) {
                if (vw == null || vw.weight() == 0f) continue;

                int boneIndex = vw.boneIndex();
                if (boneIndex < 0 || boneIndex >= normalMatrices.length) continue;

                dst.set(src).mul(normalMatrices[boneIndex]);

                float w = vw.weight();
                ox += dst.x * w;
                oy += dst.y * w;
                oz += dst.z * w;
                total += w;
            }

            if (total <= 0f) {
                out[p] = src.x;
                out[p + 1] = src.y;
                out[p + 2] = src.z;
            } else {
                if (Math.abs(total - 1f) > 0.0001f) {
                    ox /= total;
                    oy /= total;
                    oz /= total;
                }

                Vector3f n = new Vector3f(ox, oy, oz);
                if (n.lengthSquared() > 1.0e-10f) {
                    n.normalize();
                } else {
                    n.set(0f, 1f, 0f);
                }

                out[p] = n.x;
                out[p + 1] = n.y;
                out[p + 2] = n.z;
            }
        }

        return out;
    }

    private static Matrix4f[] buildSkinMatrices(
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        int count = skeleton.boneCount();
        Matrix4f[] out = new Matrix4f[count];

        for (int i = 0; i < count; i++) {
            out[i] = new Matrix4f(pose.transform(i))
                    .mul(skeleton.bone(i).inverseBindPose());
        }

        return out;
    }

    private static Matrix3f[] buildNormalMatrices(
            Skeleton skeleton,
            SkeletonPose pose
    ) {
        int count = skeleton.boneCount();
        Matrix3f[] out = new Matrix3f[count];

        for (int i = 0; i < count; i++) {
            Matrix4f skin = new Matrix4f(pose.transform(i))
                    .mul(skeleton.bone(i).inverseBindPose());

            out[i] = new Matrix3f();
            skin.normal(out[i]);
        }

        return out;
    }

    public static void printBounds(String label, float[] pos) {
        if (pos == null) {
            //System.out.println("[UMR SKIN] " + label + " bounds NULL");
            return;
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < pos.length; i += 3) {
            float x = pos[i];
            float y = pos[i + 1];
            float z = pos[i + 2];

            if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
                //System.out.println("[UMR SKIN] " + label + " has non-finite vertex at " + (i / 3));
                return;
            }

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        //System.out.println("[UMR SKIN] " + label + " bounds min=(" + minX + "," + minY + "," + minZ +
                //") max=(" + maxX + "," + maxY + "," + maxZ + ")");
    }
}