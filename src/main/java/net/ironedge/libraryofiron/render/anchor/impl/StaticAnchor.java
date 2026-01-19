package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import org.joml.Vector3f;

public final class StaticAnchor extends Anchor {

    private Vector3f offset;

    public StaticAnchor(AnchorKey key, Vector3f offset) {
        super(key, AnchorType.STATIC);
        this.offset = offset;
    }

    public Vector3f offset() {
        return offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
