package net.ironedge.libraryofiron.render.segmented;

import net.ironedge.libraryofiron.core.registry.LoIRegistry;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchor;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import net.ironedge.libraryofiron.render.pose.PoseGraphAnchorResolver;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SegmentedRenderUtil {

    private SegmentedRenderUtil() {}

    /**
     * For player body anchors like HAND_R / HAND_L
     */
    public static AnchorPoseQ resolvePlayerRoot(String poseSpaceId, AnchorKey key) {
        return PoseGraphAnchorResolver.resolveAnchor(
                poseSpaceId,
                key,
                PlayerAnchorMap.INSTANCE
        );
    }

    /**
     * For dynamic physics anchors like debug_chain_s0, debug_chain_s1, etc.
     */
    public static AnchorPoseQ resolveDynamic(FrameContextLike frame, AnchorKey key) {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return null;

        var ctx = new AnchorResolutionContext(mc.player, frame.partialTicks());
        var resolved = LoIRegistry.dynamicResolver.resolveByKey(key, ctx);
        if (resolved == null) return null;

        return new AnchorPoseQ(
                new Vector3f(resolved.transform().translation()),
                new Quaternionf(resolved.transform().rotation()),
                new Vector3f(resolved.transform().scale())
        );
    }

    public static boolean isPlayerFirstPerson() {
        var mc = Minecraft.getInstance();
        return mc != null && mc.options.getCameraType().isFirstPerson();
    }

    /**
     * Tiny adapter so we don’t drag your whole FrameContext type into helpers if you don’t want to.
     */
    public interface FrameContextLike {
        float partialTicks();
    }
}