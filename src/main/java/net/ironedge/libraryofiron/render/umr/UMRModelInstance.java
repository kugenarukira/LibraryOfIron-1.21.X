package net.ironedge.libraryofiron.render.umr;

import net.ironedge.libraryofiron.core.LoILog;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.pose.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class UMRModelInstance {

    private final String instanceId;     // unique per instance, e.g. "docock_backpack#<entityId>"
    private final UMRModelDef def;

    // Root attachment: "glue this UMR model root to some external anchor"
    private final String attachSourceId; // e.g. "player"
    private final AnchorKey attachAnchorKey; // e.g. AnchorKeys.TORSO
    private final net.ironedge.libraryofiron.render.pose.AnchorRefMap attachAnchorMap;

    // Optional extra local transform on top of the attachment anchor
    private final PoseTransform attachLocal;

    public UMRModelInstance(
            String instanceId,
            UMRModelDef def,
            String attachSourceId,
            AnchorKey attachAnchorKey,
            net.ironedge.libraryofiron.render.pose.AnchorRefMap attachAnchorMap,
            PoseTransform attachLocal
    ) {
        this.instanceId = instanceId;
        this.def = def;
        this.attachSourceId = attachSourceId;
        this.attachAnchorKey = attachAnchorKey;
        this.attachAnchorMap = attachAnchorMap;
        this.attachLocal = attachLocal;
    }

    public String sourceId() {
        // PoseGraph “source id” for this instance
        return "umr:" + instanceId;
    }

    public UMRModelDef def() { return def; }

    /**
     * Publish node poses (WORLD) into PoseGraph for this frame.
     */
    public void publish(PoseGraph graph) {
        AnchorPoseQ anchorPose = null;

        if (attachAnchorKey != null && attachAnchorMap != null) {
            anchorPose = PoseGraphAnchorResolver.resolveAnchor(
                    attachSourceId,
                    attachAnchorKey,
                    attachAnchorMap
            );
        }

        if (anchorPose == null) {
            Vector3f fallbackPos = net.ironedge.libraryofiron.render.util.EntityInterp.lerpedPos(
                    net.minecraft.client.Minecraft.getInstance().getCameraEntity(),
                    0f
            );
            anchorPose = new net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ(
                    fallbackPos,
                    new org.joml.Quaternionf(),
                    new org.joml.Vector3f(1, 1, 1)
            );
        }

        PoseTransform rootWorld = new PoseTransform(
                new Vector3f(anchorPose.pos()),
                new Quaternionf(anchorPose.rot()),
                new Vector3f(anchorPose.scale())
        );

        PoseTransform rootWithLocal = PoseMath.compose(rootWorld, attachLocal);

        Map<String, PoseTransform> worldByNode = new HashMap<>();

        for (UMRNodeDef node : def.nodes().values()) {
            PoseTransform local = new PoseTransform(
                    new Vector3f(node.localTranslation()),
                    new Quaternionf(node.localRotation()),
                    new Vector3f(node.localScale())
            );

            PoseTransform world;
            if (node.parentId() == null) {
                world = PoseMath.compose(rootWithLocal, local);
            } else {
                PoseTransform parentWorld = worldByNode.get(node.parentId());
                if (parentWorld == null) continue;
                world = PoseMath.compose(parentWorld, local);
            }

            worldByNode.put(node.id(), world);
            graph.frame().put(new PoseKey(sourceId(), node.id()), world);
        }

        publishWithRoot(graph, rootWorld);
    }

    public void publishWithRoot(PoseGraph graph, PoseTransform rootWorld) {
        PoseTransform rootWithLocal = PoseMath.compose(rootWorld, attachLocal);

        Map<String, PoseTransform> worldByNode = new HashMap<>();

        for (UMRNodeDef node : def.nodes().values()) {
            PoseTransform local = new PoseTransform(
                    new Vector3f(node.localTranslation()),
                    new Quaternionf(node.localRotation()),
                    new Vector3f(node.localScale())
            );

            PoseTransform world;
            if (node.parentId() == null) {
                world = PoseMath.compose(rootWithLocal, local);
            } else {
                PoseTransform parentWorld = worldByNode.get(node.parentId());
                if (parentWorld == null) continue;
                world = PoseMath.compose(parentWorld, local);
            }

            worldByNode.put(node.id(), world);
            graph.frame().put(new PoseKey(sourceId(), node.id()), world);
        }
    }

}
