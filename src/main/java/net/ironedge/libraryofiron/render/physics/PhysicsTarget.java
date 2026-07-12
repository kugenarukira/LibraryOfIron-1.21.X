package net.ironedge.libraryofiron.render.physics;

import net.ironedge.libraryofiron.render.core.FrameContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PhysicsTarget {
    Vector3f samplePosition(FrameContext frame);

    default Quaternionf sampleRotation(FrameContext frame) {
        return new Quaternionf();
    }
}