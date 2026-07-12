package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorTransform;

@FunctionalInterface
public interface DynamicAnchorProvider {
    AnchorTransform sample(AnchorKey key, AnchorResolutionContext ctx);
}