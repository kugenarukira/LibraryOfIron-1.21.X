package net.ironedge.libraryofiron.render.anchor;

public abstract class Anchor {

    private final AnchorKey key;
    private final AnchorType type;

    protected Anchor(AnchorKey key, AnchorType type) {
        this.key = key;
        this.type = type;
    }

    public AnchorKey key() {
        return key;
    }

    public AnchorType type() {
        return type;
    }
}
