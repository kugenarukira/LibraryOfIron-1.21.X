package net.ironedge.libraryofiron.render.anchor.path;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;

public final class PathAnchor extends Anchor {

    private final int index;

    public PathAnchor(AnchorKey key, int index) {
        super(key, AnchorType.PATH);
        this.index = index;
    }

    public int index() {
        return index;
    }
}
