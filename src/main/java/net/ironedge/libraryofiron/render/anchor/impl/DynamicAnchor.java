package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import org.joml.Vector3f;

/** Represents a dynamic/path anchor that can move procedurally. */
public final class DynamicAnchor extends Anchor {
    public DynamicAnchor(AnchorKey key, Vector3f vector3f) { super(key, AnchorType.DYNAMIC); }
}