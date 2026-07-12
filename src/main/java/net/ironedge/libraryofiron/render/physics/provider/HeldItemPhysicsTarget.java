package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsTarget;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import net.ironedge.libraryofiron.render.pose.PoseGraphAnchorResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class HeldItemPhysicsTarget implements PhysicsTarget {

    private final String poseSpaceId;
    private final AnchorRefMap anchorMap;
    private final Vector3f frontOffset;
    private final Vector3f backOffset;

    public HeldItemPhysicsTarget(
            String poseSpaceId,
            AnchorRefMap anchorMap,
            Vector3f frontOffset,
            Vector3f backOffset
    ) {
        this.poseSpaceId = poseSpaceId;
        this.anchorMap = anchorMap;
        this.frontOffset = new Vector3f(frontOffset);
        this.backOffset = new Vector3f(backOffset);
    }

    private AnchorKey resolveHandAnchor() {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return null;

        boolean mainHas = mc.player.getMainHandItem().is(Items.RED_DYE);
        boolean offHas  = mc.player.getOffhandItem().is(Items.RED_DYE);

        if (!mainHas && !offHas) return null;

        HumanoidArm mainArm = mc.player.getMainArm();

        if (mainHas) {
            return mainArm == HumanoidArm.RIGHT ? AnchorKeys.HAND_R : AnchorKeys.HAND_L;
        } else {
            return mainArm == HumanoidArm.RIGHT ? AnchorKeys.HAND_L : AnchorKeys.HAND_R;
        }
    }

    private AnchorPoseQ resolve() {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return null;

        AnchorKey handKey = resolveHandAnchor();
        if (handKey == null) return null;

        AnchorPoseQ ap = PoseGraphAnchorResolver.resolveAnchor(poseSpaceId, handKey, anchorMap);
        if (ap == null) return null;

        boolean daggerSide = mc.player.isShiftKeyDown();
        Vector3f local = daggerSide ? new Vector3f(backOffset) : new Vector3f(frontOffset);
        Vector3f worldOffset = local.rotate(new Quaternionf(ap.rot()));

        return new AnchorPoseQ(
                new Vector3f(ap.pos()).add(worldOffset),
                new Quaternionf(ap.rot()),
                new Vector3f(ap.scale())
        );
    }

    @Override
    public Vector3f samplePosition(FrameContext frame) {
        AnchorPoseQ ap = resolve();
        return ap != null ? new Vector3f(ap.pos()) : null;
    }

    @Override
    public Quaternionf sampleRotation(FrameContext frame) {
        AnchorPoseQ ap = resolve();
        return ap != null ? new Quaternionf(ap.rot()) : new Quaternionf();
    }
}