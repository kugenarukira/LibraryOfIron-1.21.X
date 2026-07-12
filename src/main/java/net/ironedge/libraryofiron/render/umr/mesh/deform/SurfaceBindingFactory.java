package net.ironedge.libraryofiron.render.umr.mesh.deform;

import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;

import java.util.ArrayList;
import java.util.List;

public final class SurfaceBindingFactory {
    private SurfaceBindingFactory() {}

    public static WeightedSurfaceMeshBinding fromUVs(
            String simulationId,
            SurfaceTopology topology,
            MeshSurface surface
    ) {
        return fromUVs(simulationId, topology, surface, false, false, false);
    }

    public static WeightedSurfaceMeshBinding fromUVs(
            String simulationId,
            SurfaceTopology topology,
            MeshSurface surface,
            boolean swapUV,
            boolean flipU,
            boolean flipV
    ) {
        float[] uvs = surface.uvsUnsafe();
        if (uvs == null) {
            throw new IllegalArgumentException("Cannot build weighted surface binding from null UVs: " + surface.name());
        }

        int vertexCount = surface.vertexCount();
        if (uvs.length != vertexCount * 2) {
            throw new IllegalArgumentException("UV count mismatch for surface: " + surface.name());
        }

        List<WeightedSurfaceVertexBinding> bindings = new ArrayList<>(vertexCount);

        int maxRowCell = topology.rows() - 2;
        int maxColCell = topology.cols() - 2;

        for (int i = 0; i < vertexCount; i++) {
            int uvBase = i * 2;

            float uTex = clamp01(uvs[uvBase]);
            float vTex = clamp01(uvs[uvBase + 1]);

            if (flipU) uTex = 1f - uTex;
            if (flipV) vTex = 1f - vTex;

            float rowCoord = swapUV ? vTex : uTex;
            float colCoord = swapUV ? uTex : vTex;

            float rowF = rowCoord * (topology.rows() - 1);
            float colF = colCoord * (topology.cols() - 1);

            int row0 = (int) Math.floor(rowF);
            int col0 = (int) Math.floor(colF);

            if (row0 > maxRowCell) row0 = maxRowCell;
            if (col0 > maxColCell) col0 = maxColCell;
            if (row0 < 0) row0 = 0;
            if (col0 < 0) col0 = 0;

            float localU = rowF - row0;
            float localV = colF - col0;

            if (row0 == maxRowCell && rowF >= topology.rows() - 1) localU = 1f;
            if (col0 == maxColCell && colF >= topology.cols() - 1) localV = 1f;

            bindings.add(new WeightedSurfaceVertexBinding(row0, col0, localU, localV));
        }

        return new WeightedSurfaceMeshBinding(
                simulationId,
                topology,
                surface.name(),
                bindings
        );
    }

    private static float clamp01(float f) {
        return f < 0f ? 0f : Math.min(f, 1f);
    }
}