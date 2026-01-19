package net.ironedge.libraryofiron.render.pose.sources;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.pose.*;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PlayerModelPoseSource implements PoseSource {

    @Override
    public void capture(FrameContext frame, PoseGraph graph) {
        Entity e = frame.attachment("cameraEntity", Entity.class);
        if (e == null) return;

        // STUB: put identity nodes so anchor resolver can test PoseGraph path.
        // Next phase: extract real ModelPart transforms.
        graph.frame().put(new PoseKey("player", "Root"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "Head"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "Body"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "RightArm"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "LeftArm"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "RightLeg"), PoseTransform.identity());
        graph.frame().put(new PoseKey("player", "LeftLeg"), PoseTransform.identity());
    }
}
