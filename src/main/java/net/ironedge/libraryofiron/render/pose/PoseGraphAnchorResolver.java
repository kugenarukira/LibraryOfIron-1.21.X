package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PoseGraphAnchorResolver {

    private PoseGraphAnchorResolver() {}

    public static AnchorPoseQ resolveAnchor(String poseSpaceId, AnchorKey key, AnchorRefMap map) {
        var ref = map.get(key);
        if (ref == null) {
           return null;
        }

        PoseKey pk = new PoseKey(poseSpaceId, ref.nodeId());
        PoseTransform bone = PoseGraph.get().frame().get(pk);

        if (bone == null) {
            return null;
        }

        Vector3f offsetWorld = new Vector3f(ref.localOffset()).rotate(bone.rotation());
        Vector3f pos = new Vector3f(bone.translation()).add(offsetWorld);

        return new AnchorPoseQ(pos, new Quaternionf(bone.rotation()), new Vector3f(1, 1, 1));
    }

    public static AnchorPoseQ resolveAnchor(String poseSpaceId, AnchorKey key) {
        return resolveAnchor(poseSpaceId, key, PlayerAnchorMap.INSTANCE);
    }

    public static AnchorPoseQ resolvePlayerAnchor(AnchorKey key) {
        return resolveAnchor("player", key, PlayerAnchorMap.INSTANCE);
    }

}