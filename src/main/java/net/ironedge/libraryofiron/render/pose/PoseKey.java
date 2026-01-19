package net.ironedge.libraryofiron.render.pose;

import java.util.Objects;

public record PoseKey(String sourceId, String nodeId) {
    public PoseKey {
        Objects.requireNonNull(sourceId);
        Objects.requireNonNull(nodeId);
    }
}
