package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import org.joml.Vector3f;

public interface AnchorRefMap {
    record BoneRef(String nodeId, Vector3f localOffset) {}

    BoneRef get(net.ironedge.libraryofiron.render.anchor.AnchorKey key);
}