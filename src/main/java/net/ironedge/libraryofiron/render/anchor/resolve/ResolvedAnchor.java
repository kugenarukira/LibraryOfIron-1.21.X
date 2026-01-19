package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.Anchor;

public final class ResolvedAnchor {

    private final Anchor anchor;
    private final AnchorTransform transform;

    public ResolvedAnchor(Anchor anchor, AnchorTransform transform) {
        this.anchor = anchor;
        this.transform = transform;
    }

    public Anchor anchor() {
        return anchor;
    }

    public AnchorTransform transform() {
        return transform;
    }
}
