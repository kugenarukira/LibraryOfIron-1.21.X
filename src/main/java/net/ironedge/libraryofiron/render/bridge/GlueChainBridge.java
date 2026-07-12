package net.ironedge.libraryofiron.render.bridge;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseMath;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.ironedge.libraryofiron.render.umr.UMRModelInstance;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;



public final class GlueChainBridge {

    private final String id;
    private final Endpoint start;
    private final Endpoint end;

    private final SegmentDef segmentDef;
    private final int segmentCount;

    private final List<UMRModelInstance> segments = new ArrayList<>();

    public GlueChainBridge(String id, Endpoint start, Endpoint end, SegmentDef segmentDef, int segmentCount) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.segmentDef = segmentDef;
        this.segmentCount = segmentCount;

        // Pre-create segment instances (each will publish with its own sourceId)
        for (int i = 0; i < segmentCount; i++) {
            UMRModelInstance inst = new UMRModelInstance(
                    "bridge:" + id + ":seg" + i,
                    segmentDef.model(),
                    "player",
                    null,
                    net.ironedge.libraryofiron.render.pose.PlayerAnchorMap.INSTANCE,
                    PoseTransform.identity()
            );
            segments.add(inst);
        }
    }

    public String id() { return id; }

    public void solveAndPublish(FrameContext frame, PoseGraph graph) {
        PoseTransform s = start.resolve(frame);
        PoseTransform e = end.resolve(frame);
        if (s == null || e == null) return;

        PoseTransform restTip = segmentDef.restTipFromRoot();

        // Phase 0: orient each segment root toward end, glue chain by restTip
        PoseTransform currentRoot = s;

        for (int i = 0; i < segments.size(); i++) {
            // Aim root rotation at end point (simple look rotation)
            PoseTransform aimedRoot = aimAt(currentRoot, e);

            // Publish this segment with the computed root
            segments.get(i).publishWithRoot(graph, aimedRoot);

            // Compute tip world pose: root ∘ restTip
            PoseTransform tipWorld = PoseMath.compose(aimedRoot, restTip);

            // Next segment root is glued to this tip
            currentRoot = tipWorld;
        }
    }

    private static PoseTransform aimAt(PoseTransform root, PoseTransform end) {
        Vector3f from = root.translation();
        Vector3f to = end.translation();

        Vector3f dir = new Vector3f(to).sub(from);
        if (dir.lengthSquared() < 1.0e-6) return root;

        // If you want a "grounded" chain (no vertical bending), uncomment:
        // dir.y = 0f;

        dir.normalize();

        // Rotate local +Z to point along dir
        org.joml.Quaternionf q = new org.joml.Quaternionf()
                .rotationTo(new org.joml.Vector3f(0, 0, 1), dir);

        return new PoseTransform(
                new Vector3f(root.translation()),
                q,
                new org.joml.Vector3f(root.scale())
        );
    }
}
