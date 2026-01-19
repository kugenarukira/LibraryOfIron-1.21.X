package net.ironedge.libraryofiron.render.anchor.resolve;

import org.joml.Matrix4f;

public class AnchorTransformContext {
    private final Matrix4f parentTransform;

    public AnchorTransformContext(Matrix4f parentTransform) {
        this.parentTransform = parentTransform;
    }

    public Matrix4f parentTransform() {
        return parentTransform;
    }
}