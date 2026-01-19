package net.ironedge.libraryofiron.render.model.part;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;

public record AttachRule(AnchorKey anchor) {
    public static AttachRule of(AnchorKey key) {
        return new AttachRule(key);
    }
}
