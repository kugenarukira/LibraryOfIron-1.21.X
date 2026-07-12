package net.ironedge.libraryofiron.render.pose.sources;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.PoseGraph;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class FirstPersonPoseSource {

    private static final float PX = 1f / 16f;

    private FirstPersonPoseSource() {}

    public static void capture(FrameContext frame, PoseGraph graph) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;

        LocalPlayer p = mc.player;

        Vec3 pos = p.getPosition(frame.partialTicks());

        Vector3f feetPos = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
        Vector3f headPos = new Vector3f(feetPos).add(0f, p.getEyeHeight(), 0f);

        float bodyYawDeg = p.yBodyRotO + (p.yBodyRot - p.yBodyRotO) * frame.partialTicks();
        Quaternionf bodyRot = new Quaternionf().rotateY((float) Math.toRadians(-bodyYawDeg));

        float yaw = p.getViewYRot(frame.partialTicks());
        float pitch = p.getViewXRot(frame.partialTicks());
        Quaternionf headRot = new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch));

        // Body/root nodes
        Vector3f bodyPos = new Vector3f(headPos).add(0f, 0f * PX, 0f);

        // ---- KEY FIX ----
        // Use a hand-following arm basis, not plain bodyRot/headRot.
        // rotateX(180) makes local +Y run DOWN the arm, which matches PlayerAnchorMap's ±(1,8,0) hand offsets.
        Quaternionf armRot = new Quaternionf(headRot).rotateX((float) Math.PI);

        // Desired final HAND anchor positions in first-person space.
        // These are the only values you should need to tune now.
        //
        // Since your right-hand attachment is currently on the correct SCREEN side already,
        // keep R negative X and L positive X in this FP camera convention.
        Vector3f desiredHandR = new Vector3f(-5f * PX, -6f * PX, -2f * PX).rotate(headRot).add(headPos);
        Vector3f desiredHandL = new Vector3f( 5f * PX, -6f * PX, -2f * PX).rotate(headRot).add(headPos);

        // These MUST match PlayerAnchorMap:
        // HAND_R = RightArm + px(-1, 8, 0) in arm local space
        // HAND_L = LeftArm  + px( 1, 8, 0) in arm local space
        Vector3f handLocalR = new Vector3f(-1f * PX, 8f * PX, 0f).rotate(armRot);
        Vector3f handLocalL = new Vector3f( 1f * PX, 8f * PX, 0f).rotate(armRot);

        // Back-solve shoulder pivots so PoseGraphAnchorResolver lands exactly on the desired hand anchors
        Vector3f shoulderR = new Vector3f(desiredHandR).sub(handLocalR);
        Vector3f shoulderL = new Vector3f(desiredHandL).sub(handLocalL);

        // Legs/hips can stay coarse for now
        Vector3f hipR = new Vector3f( 2f * PX, -22f * PX, 0f).rotate(bodyRot).add(headPos);
        Vector3f hipL = new Vector3f(-2f * PX, -22f * PX, 0f).rotate(bodyRot).add(headPos);

        graph.frame().put(new PoseKey("player", "Head"),
                new PoseTransform(headPos, headRot, new Vector3f(1, 1, 1)));

        graph.frame().put(new PoseKey("player", "Body"),
                new PoseTransform(bodyPos, bodyRot, new Vector3f(1, 1, 1)));

        graph.frame().put(new PoseKey("player", "RightArm"),
                new PoseTransform(shoulderR, armRot, new Vector3f(1, 1, 1)));

        graph.frame().put(new PoseKey("player", "LeftArm"),
                new PoseTransform(shoulderL, armRot, new Vector3f(1, 1, 1)));

        graph.frame().put(new PoseKey("player", "RightLeg"),
                new PoseTransform(hipR, bodyRot, new Vector3f(1, 1, 1)));

        graph.frame().put(new PoseKey("player", "LeftLeg"),
                new PoseTransform(hipL, bodyRot, new Vector3f(1, 1, 1)));
    }
}