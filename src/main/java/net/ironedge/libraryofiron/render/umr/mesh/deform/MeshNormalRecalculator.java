package net.ironedge.libraryofiron.render.umr.mesh.deform;

public final class MeshNormalRecalculator {

    private MeshNormalRecalculator() {}

    public static float[] recomputeNormals(float[] positions, int[] indices) {
        if (positions == null || positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions must be xyz-packed");
        }
        if (indices == null || indices.length % 3 != 0) {
            throw new IllegalArgumentException("indices must be triangle-packed");
        }

        float[] normals = new float[positions.length];

        for (int i = 0; i < indices.length; i += 3) {
            int ia = indices[i];
            int ib = indices[i + 1];
            int ic = indices[i + 2];

            int aBase = ia * 3;
            int bBase = ib * 3;
            int cBase = ic * 3;

            float ax = positions[aBase];
            float ay = positions[aBase + 1];
            float az = positions[aBase + 2];

            float bx = positions[bBase];
            float by = positions[bBase + 1];
            float bz = positions[bBase + 2];

            float cx = positions[cBase];
            float cy = positions[cBase + 1];
            float cz = positions[cBase + 2];

            float abx = bx - ax;
            float aby = by - ay;
            float abz = bz - az;

            float acx = cx - ax;
            float acy = cy - ay;
            float acz = cz - az;

            // face normal = AB x AC
            float nx = aby * acz - abz * acy;
            float ny = abz * acx - abx * acz;
            float nz = abx * acy - aby * acx;

            // accumulate to all 3 vertices
            normals[aBase]     += nx;
            normals[aBase + 1] += ny;
            normals[aBase + 2] += nz;

            normals[bBase]     += nx;
            normals[bBase + 1] += ny;
            normals[bBase + 2] += nz;

            normals[cBase]     += nx;
            normals[cBase + 1] += ny;
            normals[cBase + 2] += nz;
        }

        // normalize per vertex
        for (int i = 0; i < normals.length; i += 3) {
            float nx = normals[i];
            float ny = normals[i + 1];
            float nz = normals[i + 2];

            float lenSq = nx * nx + ny * ny + nz * nz;
            if (lenSq < 1.0e-12f) {
                normals[i] = 0f;
                normals[i + 1] = 1f;
                normals[i + 2] = 0f;
                continue;
            }

            float invLen = (float) (1.0 / Math.sqrt(lenSq));
            normals[i] *= invLen;
            normals[i + 1] *= invLen;
            normals[i + 2] *= invLen;
        }

        return normals;
    }
}