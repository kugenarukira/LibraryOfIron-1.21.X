package net.ironedge.libraryofiron.render.physics.surface;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.provider.PoseGraphPhysicsTarget;
import net.ironedge.libraryofiron.render.physics.verlet.PinConstraint;
import net.ironedge.libraryofiron.render.pose.AnchorRefMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SurfacePinPresets {

    private SurfacePinPresets() {}

    public static void pinTopRowToAnchor(
            PhysicsSimulation sim,
            SurfaceTopology topo,
            String poseSpaceId,
            AnchorKey anchor,
            AnchorRefMap anchorMap,
            float rowSpacing,
            Vector3f baseOffset
    ) {
        float half = (topo.rows() - 1) * rowSpacing * 0.5f;

        for (int row = 0; row < topo.rows(); row++) {
            final float x = row * rowSpacing - half;

            PoseGraphPhysicsTarget target = new PoseGraphPhysicsTarget(
                    poseSpaceId,
                    anchor,
                    anchorMap
            ) {
                @Override
                public Vector3f samplePosition(net.ironedge.libraryofiron.render.core.FrameContext frame) {
                    Vector3f base = super.samplePosition(frame);
                    if (base == null) return null;

                    Quaternionf rot = sampleRotation(frame);
                    Vector3f local = new Vector3f(baseOffset).add(x, 0f, 0f).rotate(rot);
                    return base.add(local);
                }
            };

            int idx = topo.index(row, 0);
            PinConstraint pin = new PinConstraint(idx, target);
            pin.inheritTargetMotion = 0.0f;
            pin.neighborMotionInfluence = 0.0f;
            pin.rotationalInfluencePoints = 0;

            sim.constraints().add(pin);
            sim.points().get(idx).pinned = true;
        }
    }

    public static void pinLeftEdgeToAnchor(
            PhysicsSimulation sim,
            SurfaceTopology topo,
            String poseSpaceId,
            AnchorKey anchor,
            AnchorRefMap anchorMap,
            Vector3f baseOffset,
            float colSpacing
    ) {
        PoseGraphPhysicsTarget target = new PoseGraphPhysicsTarget(
                poseSpaceId,
                anchor,
                anchorMap
        );

        for (int col = 0; col < topo.cols(); col++) {
            final float yOff = -col * colSpacing;

            int idx = topo.index(0, col);

            PinConstraint pin = new PinConstraint(idx, new PoseGraphPhysicsTarget(
                    poseSpaceId,
                    anchor,
                    anchorMap
            ) {
                @Override
                public Vector3f samplePosition(FrameContext frame) {
                    Vector3f pos = target.samplePosition(frame);
                    if (pos == null) return null;

                    // stable edge direction: world down
                    return new Vector3f(pos)
                            .add(baseOffset.x, baseOffset.y + yOff, baseOffset.z);
                }

                @Override
                public Quaternionf sampleRotation(FrameContext frame) {
                    return target.sampleRotation(frame);
                }
            });

            pin.inheritTargetMotion = 0.0f;
            pin.neighborMotionInfluence = 0.0f;
            pin.rotationalInfluencePoints = 0;

            sim.constraints().add(pin);
            sim.points().get(idx).pinned = true;
        }
    }

    public static void pinRightEdgeToAnchor(
            PhysicsSimulation sim,
            SurfaceTopology topo,
            String poseSpaceId,
            AnchorKey anchor,
            AnchorRefMap anchorMap,
            Vector3f baseOffset,
            float colSpacing
    ) {
        int lastRow = topo.rows() - 1;

        PoseGraphPhysicsTarget target = new PoseGraphPhysicsTarget(
                poseSpaceId,
                anchor,
                anchorMap
        );

        for (int col = 0; col < topo.cols(); col++) {
            final float yOff = -col * colSpacing;

            int idx = topo.index(lastRow, col);

            PinConstraint pin = new PinConstraint(idx, new PoseGraphPhysicsTarget(
                    poseSpaceId,
                    anchor,
                    anchorMap
            ) {
                @Override
                public Vector3f samplePosition(FrameContext frame) {
                    Vector3f pos = target.samplePosition(frame);
                    if (pos == null) return null;

                    // stable edge direction: world down
                    return new Vector3f(pos)
                            .add(baseOffset.x, baseOffset.y + yOff, baseOffset.z);
                }

                @Override
                public Quaternionf sampleRotation(FrameContext frame) {
                    return target.sampleRotation(frame);
                }
            });

            pin.inheritTargetMotion = 0.0f;
            pin.neighborMotionInfluence = 0.0f;
            pin.rotationalInfluencePoints = 0;

            sim.constraints().add(pin);
            sim.points().get(idx).pinned = true;
        }
    }

    public static void pinTopRowToShoulders(
            PhysicsSimulation sim,
            SurfaceTopology topo,
            String poseSpaceId,
            AnchorRefMap anchorMap,
            float rowSpacing
    ) {
        PoseGraphPhysicsTarget torso = new PoseGraphPhysicsTarget(
                poseSpaceId,
                AnchorKeys.TORSO,
                anchorMap
        );

        float half = (topo.rows() - 1) * rowSpacing * 0.5f;

        for (int row = 0; row < topo.rows(); row++) {
            final float seamX = row * rowSpacing - half;

            PoseGraphPhysicsTarget seamTarget = new PoseGraphPhysicsTarget(
                    poseSpaceId,
                    AnchorKeys.TORSO,
                    anchorMap
            ) {
                @Override
                public Vector3f samplePosition(FrameContext frame) {
                    Vector3f c = torso.samplePosition(frame);
                    if (c == null) return null;

                    Quaternionf torsoRot = torso.sampleRotation(frame);

                    // stable cape seam in torso-local space
                    Vector3f local = new Vector3f(seamX, -0.04f, -0.14f).rotate(torsoRot);

                    return new Vector3f(c).add(local);
                }

                @Override
                public Quaternionf sampleRotation(FrameContext frame) {
                    return torso.sampleRotation(frame);
                }
            };

            int idx = topo.index(row, 0);

            PinConstraint pin = new PinConstraint(idx, seamTarget);
            pin.inheritTargetMotion = 0.0f;
            pin.neighborMotionInfluence = 0.0f;
            pin.rotationalInfluencePoints = 0;

            sim.constraints().add(pin);
            sim.points().get(idx).pinned = true;
        }
    }
}