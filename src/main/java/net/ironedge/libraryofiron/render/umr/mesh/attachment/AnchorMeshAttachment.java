package net.ironedge.libraryofiron.render.umr.mesh.attachment;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AnchorMeshAttachment implements MeshAttachment {

    private final PoseGraphPhysicsTarget target;
    private final Vector3f localOffset;

    public AnchorMeshAttachment(
            String poseSpaceId,
            AnchorKey anchor,
            AnchorRefMap map,
            Vector3f localOffset
    ) {
        this.target = new PoseGraphPhysicsTarget(poseSpaceId, anchor, map);
        this.localOffset = new Vector3f(localOffset);
    }

    @Override
    public Vector3f samplePosition(FrameContext frame) {
        Vector3f base = target.samplePosition(frame);
        if (base == null) return null;

        Quaternionf rot = target.sampleRotation(frame);
        Vector3f offset = new Vector3f(localOffset).rotate(rot);

        return new Vector3f(base).add(offset);
    }

    @Override
    public Quaternionf sampleRotation(FrameContext frame) {
        return target.sampleRotation(frame);
    }
}