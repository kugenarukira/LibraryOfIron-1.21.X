package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.pose.PoseMath;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.ironedge.libraryofiron.render.umr.UMRModelDef;
import net.ironedge.libraryofiron.render.umr.UMRNodeDef;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SegmentDef {

    private final UMRModelDef model;
    private final String rootNodeId;
    private final String tipNodeId;

    public SegmentDef(UMRModelDef model, String rootNodeId, String tipNodeId) {
        this.model = model;
        this.rootNodeId = rootNodeId;
        this.tipNodeId = tipNodeId;
    }

    public UMRModelDef model() { return model; }
    public String rootNodeId() { return rootNodeId; }
    public String tipNodeId() { return tipNodeId; }

    /**
     * Rest transform of tip in ROOT space (root->tip), computed from model def hierarchy.
     * Works even if root is not identity in model space.
     */
    public PoseTransform restTipFromRoot() {
        PoseTransform rootModel = computeModelSpace(rootNodeId);
        PoseTransform tipModel = computeModelSpace(tipNodeId);
        return PoseMath.relative(rootModel, tipModel);
    }

    /** Rest length from root to tip in model units (blocks). */
    public float nominalLength() {
        return restTipFromRoot().translation().length();
    }

    private PoseTransform computeModelSpace(String nodeId) {
        UMRNodeDef node = model.node(nodeId);
        if (node == null) return PoseTransform.identity();

        PoseTransform local = new PoseTransform(
                new Vector3f(node.localTranslation()),
                new Quaternionf(node.localRotation()),
                new Vector3f(node.localScale())
        );

        if (node.parentId() == null) return local;

        PoseTransform parent = computeModelSpace(node.parentId());
        return PoseMath.compose(parent, local);
    }
}
