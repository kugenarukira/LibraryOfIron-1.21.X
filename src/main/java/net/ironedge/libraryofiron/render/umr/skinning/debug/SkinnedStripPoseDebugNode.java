package net.ironedge.libraryofiron.render.umr.skinning.debug;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import org.joml.Matrix4f;

public final class SkinnedStripPoseDebugNode extends RenderNode {

    public SkinnedStripPoseDebugNode() {
        super(RenderPhase.DEBUG);
    }

    @Override
    public void render(FrameContext frame) {
        if (SkinnedStripDebugContent.instance == null) return;
        if (SkinnedStripDebugContent.result == null) return;
        if (SkinnedStripDebugContent.instance.skeletonPose() == null) return;

        float age = 0f;
        var level = frame.attachment("level", net.minecraft.world.level.Level.class);
        if (level != null) {
            age = level.getGameTime() + frame.partialTicks();
        }

        float angle = (float) Math.sin(age * 0.08f) * (float) Math.toRadians(55.0);

        float height = 2.0f;

        SkinnedStripDebugContent.instance.skeletonPose().setTransform(
                0,
                new Matrix4f().identity()
        );

        SkinnedStripDebugContent.instance.skeletonPose().setTransform(
                1,
                new Matrix4f()
                        .identity()
                        .translate(0f, height, 0f)
                        .rotateZ(angle)
        );
    }
}