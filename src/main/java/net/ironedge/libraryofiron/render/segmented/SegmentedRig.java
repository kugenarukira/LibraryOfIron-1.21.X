package net.ironedge.libraryofiron.render.segmented;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.core.FrameContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface SegmentedRig {

    boolean isActive(FrameContext frame);

    /**
     * Root anchor for the object, e.g. HAND_R / HAND_L
     */
    AnchorKey rootAnchor(FrameContext frame);

    /**
     * Which pose space this root anchor belongs to.
     */
    default String poseSpaceId() {
        return "player";
    }

    /**
     * Local offset from the root anchor.
     */
    Vector3f rootOffset(FrameContext frame);

    /**
     * Local authoring rotation on top of root anchor.
     */
    default Quaternionf rootRotation(FrameContext frame) {
        return new Quaternionf();
    }

    /**
     * Number of physics-rendered segments to draw.
     */
    int segmentCount(FrameContext frame);

    /**
     * Anchor key for segment i, e.g. debug_chain_s0
     */
    AnchorKey segmentAnchorKey(int segmentIndex);

    /**
     * Variant selector.
     */
    SegmentVariantSelector selector();
}