package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolver;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorTransform;
import net.ironedge.libraryofiron.render.anchor.resolve.ResolvedAnchor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class DynamicAnchorResolver implements AnchorResolver<DynamicAnchor> {

    private final Map<AnchorKey, Vector3f> anchors = new HashMap<>();
    private final Map<AnchorKey, DynamicAnchorProvider> providers = new HashMap<>();

    public void registerAnchor(AnchorKey key, Vector3f offset) {
        anchors.put(key, offset);
    }

    public void registerProvider(AnchorKey key, DynamicAnchorProvider provider) {
        providers.put(key, provider);
    }

    @Override
    public Vector3f resolveOffset(AnchorKey key) {
        return anchors.getOrDefault(key, new Vector3f(0, 0, 0));
    }

    @Override
    public boolean hasAnchor(AnchorKey key) {
        return anchors.containsKey(key) || providers.containsKey(key);
    }

    @Override
    public ResolvedAnchor resolve(DynamicAnchor anchor, AnchorResolutionContext context) {
        DynamicAnchorProvider provider = providers.get(anchor.key());
        if (provider != null) {
            AnchorTransform t = provider.sample(anchor.key(), context);
            return new ResolvedAnchor(anchor, t != null ? t : AnchorTransform.identity());
        }

        Vector3f offset = new Vector3f(resolveOffset(anchor.key()));
        AnchorTransform transform = new AnchorTransform(offset, new Quaternionf(), new Vector3f(1, 1, 1));
        return new ResolvedAnchor(anchor, transform);
    }

    @Override
    public ResolvedAnchor resolveByKey(AnchorKey key, AnchorResolutionContext context) {
        DynamicAnchorProvider provider = providers.get(key);
        if (provider != null) {
            AnchorTransform t = provider.sample(key, context);
            return new ResolvedAnchor(new DynamicAnchor(key, new Vector3f()), t != null ? t : AnchorTransform.identity());
        }

        Vector3f offset = resolveOffset(key);
        return new ResolvedAnchor(
                new DynamicAnchor(key, new Vector3f(offset)),
                new AnchorTransform(new Vector3f(offset), new Quaternionf(), new Vector3f(1, 1, 1))
        );
    }
}