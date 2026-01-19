package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import org.joml.Vector3f;

/** Represents a dynamic/path anchor that can move procedurally. */
public final class DynamicAnchor extends Anchor {

    private Vector3f offset;

    public DynamicAnchor(AnchorKey key, Vector3f offset) {
        super(key, AnchorType.DYNAMIC);
        this.offset = offset;
    }

    public Vector3f offset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
