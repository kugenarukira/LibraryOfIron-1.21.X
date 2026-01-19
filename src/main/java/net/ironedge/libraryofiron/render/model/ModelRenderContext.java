package net.ironedge.libraryofiron.render.model;

import org.joml.Matrix4f;

public final class ModelRenderContext {

    private final Matrix4f parentTransform;
    private final float partialTicks;

    public ModelRenderContext(Matrix4f parentTransform, float partialTicks) {
        this.parentTransform = parentTransform;
        this.partialTicks = partialTicks;
    }

    public Matrix4f parentTransform() { return parentTransform; }
    public float partialTicks() { return partialTicks; }
}
