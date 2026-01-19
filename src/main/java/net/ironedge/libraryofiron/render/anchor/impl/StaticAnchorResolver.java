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

public final class StaticAnchorResolver implements AnchorResolver<StaticAnchor> {

    private final Map<AnchorKey, Vector3f> anchors = new HashMap<>();

    public void registerAnchor(AnchorKey key, Vector3f offset) {
        anchors.put(key, offset);
    }

    @Override
    public Vector3f resolveOffset(AnchorKey key) {
        return anchors.getOrDefault(key, new Vector3f(0, 0, 0));
    }

    @Override
    public boolean hasAnchor(AnchorKey key) {
        return anchors.containsKey(key);
    }

    @Override
    public ResolvedAnchor resolve(StaticAnchor anchor, AnchorResolutionContext context) {
        Vector3f offset = new Vector3f(anchor.offset()); // LIVE value, not map cache
        AnchorTransform transform = new AnchorTransform(offset, new Quaternionf(), new Vector3f(1, 1, 1));
        return new ResolvedAnchor(anchor, transform);
    }


    @Override
    public ResolvedAnchor resolveByKey(AnchorKey key, AnchorResolutionContext context) {
        Vector3f offset = resolveOffset(key);
        // Create a "stub" anchor just for the resolved wrapper
        StaticAnchor anchor = new StaticAnchor(key, offset);
        return new ResolvedAnchor(anchor, new AnchorTransform(offset, new Quaternionf(), new Vector3f(1,1,1)));
    }
}
