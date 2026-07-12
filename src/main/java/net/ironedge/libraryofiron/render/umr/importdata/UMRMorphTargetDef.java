package net.ironedge.libraryofiron.render.umr.importdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class UMRMorphTargetDef {

    private final String name;
    private final Map<String, float[]> positionDeltasBySurface;

    public UMRMorphTargetDef(String name, Map<String, float[]> positionDeltasBySurface) {
        this.name = name;
        this.positionDeltasBySurface = new HashMap<>();

        if (positionDeltasBySurface != null) {
            for (var e : positionDeltasBySurface.entrySet()) {
                this.positionDeltasBySurface.put(
                        e.getKey(),
                        Arrays.copyOf(e.getValue(), e.getValue().length)
                );
            }
        }
    }

    public String name() {
        return name;
    }

    public Map<String, float[]> positionDeltasBySurface() {
        Map<String, float[]> out = new HashMap<>();
        for (var e : positionDeltasBySurface.entrySet()) {
            out.put(e.getKey(), Arrays.copyOf(e.getValue(), e.getValue().length));
        }
        return out;
    }
}