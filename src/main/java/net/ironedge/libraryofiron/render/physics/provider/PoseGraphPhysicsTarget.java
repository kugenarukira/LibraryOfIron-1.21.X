package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsTarget;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PoseGraphAnchorResolver;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PoseGraphPhysicsTarget implements PhysicsTarget {

    private final String poseSpaceId;
    private final AnchorKey anchorKey;
    private final AnchorRefMap anchorMap;

    public PoseGraphPhysicsTarget(String poseSpaceId, AnchorKey anchorKey, AnchorRefMap anchorMap) {
        this.poseSpaceId = poseSpaceId;
        this.anchorKey = anchorKey;
        this.anchorMap = anchorMap;
    }

    @Override
    public Vector3f samplePosition(FrameContext frame) {
        AnchorPoseQ ap = PoseGraphAnchorResolver.resolveAnchor(poseSpaceId, anchorKey, anchorMap);
        if (ap == null) return null;
        return new Vector3f(ap.pos());
    }

    @Override
    public Quaternionf sampleRotation(FrameContext frame) {
        AnchorPoseQ ap = PoseGraphAnchorResolver.resolveAnchor(poseSpaceId, anchorKey, anchorMap);
        if (ap == null) return new Quaternionf();
        return new Quaternionf(ap.rot());
    }
}