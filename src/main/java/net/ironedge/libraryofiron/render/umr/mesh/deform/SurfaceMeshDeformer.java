package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.physics.surface.PhysicsSurfaceView;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import org.joml.Vector3f;

public final class SurfaceMeshDeformer {

    private SurfaceMeshDeformer() {}

    public static float[] deformPositionsWorld(
            MeshSurface surface,
            SurfaceMeshBinding binding
    ) {
        SurfaceTopology topo = binding.topology();
        int expectedVerts = topo.pointCount();

        if (surface.vertexCount() != expectedVerts) {
            throw new IllegalStateException(
                    "Surface mesh vertex count mismatch: mesh surface '" + surface.name() +
                            "' has " + surface.vertexCount() +
                            " vertices but topology requires " + expectedVerts
            );
        }

        float[] out = new float[expectedVerts * 3];

        for (int row = 0; row < topo.rows(); row++) {
            for (int col = 0; col < topo.cols(); col++) {
                int vi = topo.index(row, col);

                Vector3f p = PhysicsSurfaceView.point(binding.simulationId(), topo, row, col);
                if (p == null) {
                    throw new IllegalStateException(
                            "Missing simulation point for surface binding at row=" + row + ", col=" + col
                    );
                }

                int base = vi * 3;
                out[base] = p.x;
                out[base + 1] = p.y;
                out[base + 2] = p.z;
            }
        }

        return out;
    }
}