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
    public static ResolvedAnchor resolveAnchor(
            Anchor anchor,
            AnchorResolutionContext context
    ) {
        return getResolver(anchor.type())
                .map(resolver -> resolver.resolve(anchor, context))
                .orElseGet(() -> {
                    // fallback identity transform if no resolver registered
                    return new ResolvedAnchor(anchor, AnchorTransform.identity());
                });
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
