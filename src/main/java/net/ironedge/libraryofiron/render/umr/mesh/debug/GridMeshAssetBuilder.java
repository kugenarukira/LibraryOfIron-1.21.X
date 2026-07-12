package net.ironedge.libraryofiron.render.umr.mesh.debug;

import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;

import java.util.List;
import java.util.Map;

public final class GridMeshAssetBuilder {

    private GridMeshAssetBuilder() {}

    public static MeshAsset buildGrid(
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

        int indexCount = (rows - 1) * (cols - 1) * 6;
        int[] indices = new int[indexCount];

        float halfW = width * 0.5f;

        for (int row = 0; row < rows; row++) {
            float u = (float) row / (float) (rows - 1);
            float x = -halfW + u * width;

            for (int col = 0; col < cols; col++) {
                float v = (float) col / (float) (cols - 1);
                float y = -v * height;

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
                uvs[uv + 1] = v;
            }
        }

        int ii = 0;
        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols - 1; col++) {
                int a = row * cols + col;
                int b = (row + 1) * cols + col;
                int c = (row + 1) * cols + (col + 1);
                int d = row * cols + (col + 1);

                indices[ii++] = a;
                indices[ii++] = c;
                indices[ii++] = b;

                indices[ii++] = a;
                indices[ii++] = d;
                indices[ii++] = c;
            }
        }

        System.out.println(
                "[UMR GRID] Verts=" + (positions.length / 3)
                        + " UVs=" + (uvs.length / 2)
                        + " Tris=" + (indices.length / 3)
        );

        for (int i = 0; i < Math.min(10, positions.length / 3); i++) {
            System.out.println(
                    "[UMR GRID] v" + i +
                            " pos=(" +
                            positions[i * 3] + ", " +
                            positions[i * 3 + 1] + ", " +
                            positions[i * 3 + 2] + ")" +
                            " uv=(" +
                            uvs[i * 2] + ", " +
                            uvs[i * 2 + 1] + ")"
            );
        }

        MeshSurface surface = new MeshSurface(
                surfaceName,
                positions,
                normals,
                uvs,
                indices,
                "default"
        );

        return new MeshAsset(
                assetId,
                List.of(surface),
                Map.of()
        );
    }
}