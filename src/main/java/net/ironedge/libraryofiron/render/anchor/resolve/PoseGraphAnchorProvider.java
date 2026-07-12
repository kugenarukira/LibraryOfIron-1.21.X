package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorProvider;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PoseGraphAnchorProvider implements DynamicAnchorProvider {

    private final String poseSpaceId;
    private final AnchorRefMap anchorMap;

    public PoseGraphAnchorProvider(String poseSpaceId, AnchorRefMap anchorMap) {
        this.poseSpaceId = poseSpaceId;
        this.anchorMap = anchorMap;
    }

    @Override
    public AnchorTransform sample(AnchorKey key, AnchorResolutionContext ctx) {
        var ref = anchorMap.get(key);
        if (ref == null) return AnchorTransform.identity();

        PoseTransform bone = PoseGraph.get().frame().get(new PoseKey(poseSpaceId, ref.nodeId()));
        if (bone == null) return AnchorTransform.identity();

        Vector3f offsetWorld = new Vector3f(ref.localOffset()).rotate(bone.rotation());
        Vector3f pos = new Vector3f(bone.translation()).add(offsetWorld);

        return new AnchorTransform(pos, new Quaternionf(bone.rotation()), new Vector3f(1,1,1));
    }
}