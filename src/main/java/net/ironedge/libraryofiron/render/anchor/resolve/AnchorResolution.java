package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.Anchor;

public final class AnchorResolution {

    private AnchorResolution() {}

    public static ResolvedAnchor resolve(Anchor anchor, AnchorResolutionContext context) {
        return AnchorResolverRegistry.resolveAnchor(anchor, context, null);
    }

    public static ResolvedAnchor resolve(
            Anchor anchor,
            AnchorResolutionContext context,
            ResolvedAnchor parent
    ) {
        return AnchorResolverRegistry.resolveAnchor(anchor, context, parent);
    }
}
