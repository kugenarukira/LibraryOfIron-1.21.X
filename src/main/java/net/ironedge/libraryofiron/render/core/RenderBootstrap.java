package net.ironedge.libraryofiron.render.core;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.model.part.AttachRule;
import net.ironedge.libraryofiron.render.model.part.PartDef;
import net.ironedge.libraryofiron.render.model.part.PartType;
import net.ironedge.libraryofiron.render.model.part.RenderCondition;
import net.ironedge.libraryofiron.render.model.registry.PartRegistry;
import net.ironedge.libraryofiron.render.model.render.ModelRenderNode;
import net.ironedge.libraryofiron.render.model.render.PartRenderer;
import net.ironedge.libraryofiron.render.physics.PhysicsSolveNode;
import net.ironedge.libraryofiron.render.physics.debug.PhysicsCurveDebugNode;
import net.ironedge.libraryofiron.render.physics.debug.PhysicsDebugDrawNode;

public final class RenderBootstrap {

    private static boolean installed = false;

    private RenderBootstrap() {}

    public static void install(RenderEngine engine) {
        if (installed) return;
        installed = true;

        var r = PartRenderer.NOOP;
        PartRegistry reg = new PartRegistry();

        // Basic anchor markers (head/spine + limbs)
        reg.register(def("test_head", PartType.BODY_EXTENSION, AnchorKeys.HEAD), r);
        reg.register(def("test_torso", PartType.ARMOR_ATTACHMENT, AnchorKeys.TORSO), r);

        reg.register(def("test_shoulder_l", PartType.BODY_EXTENSION, AnchorKeys.SHOULDER_L), r);
        reg.register(def("test_shoulder_r", PartType.BODY_EXTENSION, AnchorKeys.SHOULDER_R), r);
        reg.register(def("test_hand_l", PartType.UTILITY_ATTACHMENT, AnchorKeys.HAND_L), r);
        reg.register(def("test_hand_r", PartType.UTILITY_ATTACHMENT, AnchorKeys.HAND_R), r);

        reg.register(def("test_hip_l", PartType.BODY_EXTENSION, AnchorKeys.HIP_L), r);
        reg.register(def("test_hip_r", PartType.BODY_EXTENSION, AnchorKeys.HIP_R), r);
        reg.register(def("test_foot_l", PartType.UTILITY_ATTACHMENT, AnchorKeys.FOOT_L), r);
        reg.register(def("test_foot_r", PartType.UTILITY_ATTACHMENT, AnchorKeys.FOOT_R), r);

        engine.graph().addNode(new ModelRenderNode(reg));
        engine.graph().addNode(new PhysicsSolveNode());
        engine.graph().addNode(new PhysicsDebugDrawNode());
        engine.graph().addNode(new net.ironedge.libraryofiron.render.segmented.SegmentedRenderNode(new net.ironedge.libraryofiron.render.debug.rigs.AmatsuTestRig()));

        //net.ironedge.libraryofiron.render.physics.debug.PhysicsDebugContent.install();
        //net.ironedge.libraryofiron.render.physics.debug.AmatsuPhysicsContent.install();
    }

    private static PartDef def(String id, PartType type, net.ironedge.libraryofiron.render.anchor.AnchorKey key) {
        return new PartDef(id, type, AttachRule.of(key), RenderCondition.ALWAYS);
    }
}
