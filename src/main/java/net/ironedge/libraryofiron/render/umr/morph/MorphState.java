package net.ironedge.libraryofiron.render.umr.morph;

import java.util.HashMap;
import java.util.Map;

public final class MorphState {

    private final Map<String, Float> weights = new HashMap<>();

    public void setWeight(String morphName, float weight) {
        weights.put(morphName, weight);
    }

    public float weight(String morphName) {
        return weights.getOrDefault(morphName, 0f);
    }

    public void clear() {
        weights.clear();
    }

    public Map<String, Float> weights() {
        return new HashMap<>(weights);
    }
}