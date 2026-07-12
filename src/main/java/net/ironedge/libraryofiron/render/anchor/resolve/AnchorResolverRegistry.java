package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchor;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchor;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchorResolver;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central registry for all anchor resolvers.
 * Supports static, dynamic, and future types.
 */
public final class AnchorResolverRegistry {

    private static final Map<AnchorType, AnchorResolver<? extends Anchor>> RESOLVERS =
            new EnumMap<>(AnchorType.class);

    private AnchorResolverRegistry() {}

    /** Register a resolver for a specific type */
    public static <T extends Anchor> void registerResolver(
            AnchorType type,
            AnchorResolver<T> resolver
    ) {
        RESOLVERS.put(type, resolver);
    }

    /** Get a resolver for a specific type */
    @SuppressWarnings("unchecked")
    public static <T extends Anchor> Optional<AnchorResolver<T>> getResolver(AnchorType type) {
        return Optional.ofNullable((AnchorResolver<T>) RESOLVERS.get(type));
    }

    /**
     * Unified resolution: look up resolver by anchor type and resolve.
     */

    public static ResolvedAnchor resolveAnchor(Anchor anchor, AnchorResolutionContext context) {
        return resolveAnchor(anchor, context, null);
    }

    public static ResolvedAnchor resolveAnchor(
            Anchor anchor,
            AnchorResolutionContext context,
            ResolvedAnchor parent // nullable
    ) {
        ResolvedAnchor localResolved = getResolver(anchor.type())
                .map(r -> ((AnchorResolver<Anchor>) r).resolve(anchor, context))
                .orElse(new ResolvedAnchor(anchor, AnchorTransform.identity()));

        if (parent == null) return localResolved;

        // Compose parent + local (TRS)
        AnchorTransform p = parent.transform();
        AnchorTransform l = localResolved.transform();

        // worldRot = parentRot * localRot
        var worldRot = new org.joml.Quaternionf(p.rotation()).mul(l.rotation());

        // worldScale = parentScale * localScale (component-wise)
        var worldScale = new org.joml.Vector3f(p.scale()).mul(l.scale());

        // worldPos = parentPos + (localPos * parentScale) rotated by parentRot
        var off = new org.joml.Vector3f(l.translation()).mul(p.scale()).rotate(p.rotation());
        var worldPos = new org.joml.Vector3f(p.translation()).add(off);

        return new ResolvedAnchor(anchor, new AnchorTransform(worldPos, worldRot, worldScale));
    }

    /**
     * Unified offset fetch: returns just the offset vector of the anchor.
     */
    public static Vector3f getAnchorOffset(Anchor anchor) {
        return getResolver(anchor.type())
                .map(resolver -> resolver.resolveOffset(anchor.key()))
                .orElse(new Vector3f(0, 0, 0));
    }
}
