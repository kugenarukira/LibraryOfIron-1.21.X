package net.ironedge.libraryofiron.render.umr.mesh;

import org.joml.Vector3f;

public final class MeshBounds {

    private final Vector3f min;
    private final Vector3f max;

    public MeshBounds(Vector3f min, Vector3f max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public Vector3f min() {
        return new Vector3f(min);
    }

    public Vector3f max() {
        return new Vector3f(max);
    }

    public Vector3f size() {
        return new Vector3f(max).sub(min);
    }

    public Vector3f center() {
        return new Vector3f(min).add(max).mul(0.5f);
    }

    public static MeshBounds fromPositions(float[] positions) {
        if (positions == null || positions.length < 3 || positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions must be xyz-packed");
        }

        float minX = positions[0], minY = positions[1], minZ = positions[2];
        float maxX = positions[0], maxY = positions[1], maxZ = positions[2];

        for (int i = 3; i < positions.length; i += 3) {
            float x = positions[i];
            float y = positions[i + 1];
            float z = positions[i + 2];

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

        return new MeshBounds(
                new Vector3f(minX, minY, minZ),
                new Vector3f(maxX, maxY, maxZ)
        );
    }
}