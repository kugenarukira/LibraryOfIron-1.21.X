package net.ironedge.libraryofiron.render.umr.skinning.debug;

import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.skeleton.Bone;
import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;
import net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose;
import net.ironedge.libraryofiron.render.umr.skinning.MeshSkinWeights;
import net.ironedge.libraryofiron.render.umr.skinning.VertexSkinData;
import net.ironedge.libraryofiron.render.umr.skinning.VertexWeight;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SkinnedStripBuilder {

    private SkinnedStripBuilder() {}

    public static Result build(
            String assetId,
            String surfaceName,
            int rows,
            int cols,
            float width,
            float height
    ) {
        if (rows < 2) throw new IllegalArgumentException("rows must be >= 2");
        if (cols < 2) throw new IllegalArgumentException("cols must be >= 2");

        int vertexCount = rows * cols;

        float[] positions = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] uvs = new float[vertexCount * 2];
        int[] indices = new int[(rows - 1) * (cols - 1) * 6];

        float halfW = width * 0.5f;

        List<VertexSkinData> skinData = new ArrayList<>(vertexCount);

        for (int row = 0; row < rows; row++) {
            float t = (float) row / (float) (rows - 1);
            float y = t * height;

            for (int col = 0; col < cols; col++) {
                float u = (float) col / (float) (cols - 1);
                float x = -halfW + u * width;

                int vi = row * cols + col;

                int p = vi * 3;
                positions[p] = x;
                positions[p + 1] = y;
                positions[p + 2] = 0f;

                normals[p] = 0f;
                normals[p + 1] = 0f;
                normals[p + 2] = 1f;

                int uv = vi * 2;
                uvs[uv] = u;
                uvs[uv + 1] = 1f - t;

                skinData.add(new VertexSkinData(
                        new VertexWeight(0, 1f - t),
                        new VertexWeight(1, t)
                ));
            }
        }

        int ii = 0;
        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols - 1; col++) {
                int a = row * cols + col;
                int b = row * cols + col + 1;
                int c = (row + 1) * cols + col + 1;
                int d = (row + 1) * cols + col;

                // triangle-as-quad renderer compatibility still uses triangle indices;
                // MeshRenderNode should emit triangle indices as degenerate quads if needed.
                indices[ii++] = a;
                indices[ii++] = b;
                indices[ii++] = c;

                indices[ii++] = a;
                indices[ii++] = c;
                indices[ii++] = d;
            }
        }

        MeshSurface surface = new MeshSurface(
                surfaceName,
                positions,
                normals,
                uvs,
                indices,
                "default"
        );

        surface.skinWeights(new MeshSkinWeights(skinData));

        Matrix4f rootBind = new Matrix4f().identity();
        Matrix4f tipBind = new Matrix4f().identity().translate(0f, height, 0f);

        Bone root = new Bone(
                "root",
                -1,
                rootBind,
                new Matrix4f(rootBind).invert()
        );

        Bone tip = new Bone(
                "tip",
                0,
                tipBind,
                new Matrix4f(tipBind).invert()
        );

        Skeleton skeleton = new Skeleton(List.of(root, tip));

        MeshAsset asset = new MeshAsset(
                assetId,
                List.of(surface),
                Map.of(),
                skeleton
        );

        SkeletonPose pose = new SkeletonPose(skeleton.boneCount());
        pose.setTransform(0, new Matrix4f().identity());
        pose.setTransform(1, new Matrix4f(tipBind));

        return new Result(asset, skeleton, pose);
    }

    public record Result(
            MeshAsset asset,
            Skeleton skeleton,
            SkeletonPose pose
    ) {}
}