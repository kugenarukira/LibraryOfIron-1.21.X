package net.ironedge.libraryofiron.render.pose;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PoseGraphAnchorResolver {

    private PoseGraphAnchorResolver() {}

    public static AnchorPoseQ resolvePlayerAnchor(AnchorKey key) {
        var ref = PlayerAnchorMap.get(key);
        if (ref == null) return null;

        PoseTransform bone = PoseGraph.get().frame().get(new PoseKey("player", ref.nodeId()));
        if (bone == null) return null;

        // Apply local offset in bone space: pos + (offset rotated by bone rot)
        Vector3f offsetWorld = new Vector3f(ref.localOffset()).rotate(bone.rotation());
        Vector3f pos = new Vector3f(bone.translation()).add(offsetWorld);

        return new AnchorPoseQ(pos, new Quaternionf(bone.rotation()), new Vector3f(1,1,1));
    }
}
