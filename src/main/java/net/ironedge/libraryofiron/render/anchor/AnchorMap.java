package net.ironedge.libraryofiron.render.anchor;

import java.util.HashMap;
import java.util.Map;

public final class AnchorMap {

    private final Map<AnchorKey, Anchor> anchors = new HashMap<>();

    public void register(Anchor anchor) {
        anchors.put(anchor.key(), anchor);
    }

    public Anchor get(AnchorKey key) {
        return anchors.get(key);
    }

    public boolean has(AnchorKey key) {
        return anchors.containsKey(key);
    }

    public Map<AnchorKey, Anchor> all() {
        return anchors;
    }
}
