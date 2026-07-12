package net.ironedge.libraryofiron.render.anchor.impl;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorTransform;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DynamicAnchorProviders {
    private DynamicAnchorProviders() {}

    public static void installPoseGraphProviders(DynamicAnchorResolver dyn) {
        installPoseGraphProviders(dyn, "player", PlayerAnchorMap.INSTANCE);
    }

    public static void installPoseGraphProviders(
            DynamicAnchorResolver dyn,
            String poseSpaceId,
            AnchorRefMap anchorMap
    ) {
        dyn.registerProvider(AnchorKeys.HEAD,       (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.TORSO,      (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.HIPS,       (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.SHOULDER_L, (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.SHOULDER_R, (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.HAND_L,     (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.HAND_R,     (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.HIP_L,      (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.HIP_R,      (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.FOOT_L,     (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
        dyn.registerProvider(AnchorKeys.FOOT_R,     (k, ctx) -> poseGraphSample(poseSpaceId, k, anchorMap));
    }

    private static AnchorTransform poseGraphSample(
            String poseSpaceId,
            AnchorKey key,
            AnchorRefMap anchorMap
    ) {
        var ref = anchorMap.get(key);
        if (ref == null) return AnchorTransform.identity();

        PoseTransform bone = PoseGraph.get().frame().get(new PoseKey(poseSpaceId, ref.nodeId()));
        if (bone == null) return AnchorTransform.identity();

        Vector3f offsetWorld = new Vector3f(ref.localOffset()).rotate(bone.rotation());
        Vector3f pos = new Vector3f(bone.translation()).add(offsetWorld);

        return new AnchorTransform(pos, new Quaternionf(bone.rotation()), new Vector3f(1, 1, 1));
    }
}