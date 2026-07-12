package net.ironedge.libraryofiron.render.umr.morph;

import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;

import java.util.HashMap;
import java.util.Map;

public final class MorphBlender {

    private MorphBlender() {}

    /**
     * Returns blended position arrays per surface name.
     * Base positions are copied, then morph deltas are accumulated.
     */
    public static Map<String, float[]> blendPositions(MeshAsset asset, MorphState state) {
        Map<String, float[]> blended = new HashMap<>();

        for (MeshSurface surface : asset.surfaces()) {
            blended.put(surface.name(), surface.positions());
        }

        for (var weightEntry : state.weights().entrySet()) {
            String morphName = weightEntry.getKey();
            float weight = weightEntry.getValue();

            if (Math.abs(weight) < 1.0e-6f) continue;

            MorphTarget morph = asset.morphTarget(morphName);
            if (morph == null) continue;

            for (MeshSurface surface : asset.surfaces()) {
                String surfaceName = surface.name();
                if (!morph.hasSurface(surfaceName)) continue;

                float[] base = blended.get(surfaceName);
                float[] deltas = morph.positionDeltas(surfaceName);

                if (base == null || deltas == null) continue;
                if (base.length != deltas.length) {
                    throw new IllegalStateException(
                            "Morph delta length mismatch for surface '" + surfaceName + "' in morph '" + morphName + "'"
                    );
                }

                for (int i = 0; i < base.length; i++) {
                    base[i] += deltas[i] * weight;
                }
            }
        }

        return blended;
    }
}