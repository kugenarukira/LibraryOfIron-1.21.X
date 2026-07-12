package net.ironedge.libraryofiron.render.umr.mesh.attachment;

import net.ironedge.libraryofiron.render.core.FrameContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface MeshAttachment {

    Vector3f samplePosition(FrameContext frame);

    Quaternionf sampleRotation(FrameContext frame);
}