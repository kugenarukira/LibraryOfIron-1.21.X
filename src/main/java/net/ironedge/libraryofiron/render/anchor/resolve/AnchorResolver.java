package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import org.joml.Vector3f;

public interface AnchorResolver<T extends Anchor> {

    Vector3f resolveOffset(AnchorKey key);

    boolean hasAnchor(AnchorKey key);

    ResolvedAnchor resolve(T anchor, AnchorResolutionContext context);

    /** NEW: Resolve anchor directly by key */
    ResolvedAnchor resolveByKey(AnchorKey key, AnchorResolutionContext context);
}
