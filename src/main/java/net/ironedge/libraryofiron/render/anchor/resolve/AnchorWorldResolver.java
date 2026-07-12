package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchor;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PoseGraphAnchorResolver;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AnchorWorldResolver {
    private AnchorWorldResolver() {}

    public static AnchorPoseQ resolve(
            String poseSpaceId,
            AnchorKey key,
            AnchorRefMap anchorMap,
            AnchorResolutionContext ctx
    ) {
        // 1) Try PoseGraph/AnchorRefMap route first
        if (anchorMap != null) {
            AnchorPoseQ ap = PoseGraphAnchorResolver.resolveAnchor(poseSpaceId, key, anchorMap);
            if (ap != null) return ap;
        }

        // 2) Fall back to dynamic-anchor provider route
        var resolved = AnchorResolverRegistry.resolveAnchor(new DynamicAnchor(key, new Vector3f()), ctx, null);
        if (resolved != null && resolved.transform() != null) {
            var t = resolved.transform();
            return new AnchorPoseQ(
                    new Vector3f(t.translation()),
                    new Quaternionf(t.rotation()),
                    new Vector3f(t.scale())
            );
        }

        return null;
    }
}