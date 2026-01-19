package net.ironedge.libraryofiron.render.pose;

import java.util.HashMap;
import java.util.Map;

public final class PoseFrame {
    private final Map<PoseKey, PoseTransform> poses = new HashMap<>();

    public void put(PoseKey key, PoseTransform transform) {
        poses.put(key, transform);
    }

    public PoseTransform get(PoseKey key) {
        return poses.get(key);
    }

    public boolean has(PoseKey key) {
        return poses.containsKey(key);
    }

    public void clear() {
        poses.clear();
    }
}
