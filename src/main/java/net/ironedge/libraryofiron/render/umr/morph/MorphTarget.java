package net.ironedge.libraryofiron.render.umr.morph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class MorphTarget {

    private final String name;
    private final Map<String, float[]> positionDeltasBySurface;

    public MorphTarget(String name, Map<String, float[]> positionDeltasBySurface) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (positionDeltasBySurface == null || positionDeltasBySurface.isEmpty()) {
            throw new IllegalArgumentException("position deltas must not be empty");
        }

        this.name = name;
        this.positionDeltasBySurface = new HashMap<>();
        for (var e : positionDeltasBySurface.entrySet()) {
            float[] deltas = e.getValue();
            if (deltas == null || deltas.length % 3 != 0) {
                throw new IllegalArgumentException("morph deltas must be xyz-packed");
            }
            this.positionDeltasBySurface.put(e.getKey(), Arrays.copyOf(deltas, deltas.length));
        }
    }

    public String name() {
        return name;
    }

    public boolean hasSurface(String surfaceName) {
        return positionDeltasBySurface.containsKey(surfaceName);
    }

    public float[] positionDeltas(String surfaceName) {
        float[] d = positionDeltasBySurface.get(surfaceName);
        return d != null ? Arrays.copyOf(d, d.length) : null;
    }

    public Map<String, float[]> positionDeltasBySurface() {
        Map<String, float[]> out = new HashMap<>();
        for (var e : positionDeltasBySurface.entrySet()) {
            out.put(e.getKey(), Arrays.copyOf(e.getValue(), e.getValue().length));
        }
        return out;
    }
}