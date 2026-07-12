package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorWorldResolver;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AnchorEndpoint implements Endpoint {

    private final String poseSpaceId;
    private final AnchorKey key;
    private final AnchorRefMap anchorMap;

    public AnchorEndpoint(String poseSpaceId, AnchorKey key, AnchorRefMap anchorMap) {
        this.poseSpaceId = poseSpaceId;
        this.key = key;
        this.anchorMap = anchorMap;
    }

    @Override
    public PoseTransform resolve(FrameContext frame) {
        Entity cameraEntity = frame.attachment("cameraEntity", Entity.class);
        if (cameraEntity == null) return null;

        AnchorResolutionContext arc = new AnchorResolutionContext(
                cameraEntity,
                frame.partialTicks()
        );

        var ap = AnchorWorldResolver.resolve(poseSpaceId, key, anchorMap, arc);
        if (ap == null) return null;

        return new PoseTransform(
                new Vector3f(ap.pos()),
                new Quaternionf(ap.rot()),
                new Vector3f(ap.scale())
        );
    }
}