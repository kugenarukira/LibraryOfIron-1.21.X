package net.ironedge.libraryofiron.render.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class FrameContext {

    private final float partialTicks;
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;
    private final Vector3f cameraPos;

    // Optional escape hatch: platform-specific attachments without hard deps
    private final Map<String, Object> attachments = new HashMap<>();

    public FrameContext(float partialTicks, Matrix4f viewMatrix, Matrix4f projectionMatrix, Vector3f cameraPos) {
        this.partialTicks = partialTicks;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.cameraPos = cameraPos;
    }

    public float partialTicks() {
        return partialTicks;
    }

    public Matrix4f viewMatrix() {
        return viewMatrix;
    }

    public Matrix4f projectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f cameraPos() {
        return cameraPos;
    }

    public FrameContext attach(String key, Object value) {
        attachments.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T attachment(String key, Class<T> type) {
        Object v = attachments.get(key);
        return (v != null && type.isInstance(v)) ? (T) v : null;
    }
}
