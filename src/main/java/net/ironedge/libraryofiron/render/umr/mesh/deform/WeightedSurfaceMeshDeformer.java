package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.physics.surface.PhysicsSurfaceView;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import org.joml.Vector3f;

import java.util.List;

public final class WeightedSurfaceMeshDeformer {
    private WeightedSurfaceMeshDeformer() {}

    public static float[] deformPositionsWorld(
            MeshSurface surface,
            WeightedSurfaceMeshBinding binding
    ) {
        int vertexCount = surface.vertexCount();
        List<WeightedSurfaceVertexBinding> bindings = binding.vertexBindings();

        if (bindings.size() != vertexCount) {
            throw new IllegalStateException(
                    "Weighted binding mismatch for '" + surface.name() +
                            "': verts=" + vertexCount +
                            ", bindings=" + bindings.size()
            );
        }

        SurfaceTopology topo = binding.topology();
        float[] out = new float[vertexCount * 3];

        for (int i = 0; i < vertexCount; i++) {
            WeightedSurfaceVertexBinding b = bindings.get(i);

            int r0 = b.row0();
            int c0 = b.col0();
            int r1 = r0 + 1;
            int c1 = c0 + 1;

            Vector3f p00 = PhysicsSurfaceView.point(binding.simulationId(), topo, r0, c0);
            Vector3f p10 = PhysicsSurfaceView.point(binding.simulationId(), topo, r1, c0);
            Vector3f p01 = PhysicsSurfaceView.point(binding.simulationId(), topo, r0, c1);
            Vector3f p11 = PhysicsSurfaceView.point(binding.simulationId(), topo, r1, c1);

            if (p00 == null || p10 == null || p01 == null || p11 == null) {
                continue;
            }

            float u = clamp01(b.u());
            float v = clamp01(b.v());

            Vector3f left = new Vector3f(p00).lerp(p01, v);
            Vector3f right = new Vector3f(p10).lerp(p11, v);
            Vector3f p = left.lerp(right, u);

            int base = i * 3;
            out[base] = p.x;
            out[base + 1] = p.y;
            out[base + 2] = p.z;
        }

        return out;
    }

    private static float clamp01(float f) {
        return f < 0f ? 0f : Math.min(f, 1f);
    }
}