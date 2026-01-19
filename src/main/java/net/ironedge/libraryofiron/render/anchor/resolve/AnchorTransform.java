package net.ironedge.libraryofiron.render.anchor.resolve;

import org.joml.Vector3f;
import org.joml.Quaternionf;

public class AnchorTransform {

    private final Vector3f translation;
    private final Quaternionf rotation;
    private final Vector3f scale;

    public AnchorTransform(Vector3f translation, Quaternionf rotation, Vector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f translation() { return translation; }
    public Quaternionf rotation() { return rotation; }
    public Vector3f scale() { return scale; }

    public static AnchorTransform identity() {
        return new AnchorTransform(
                new Vector3f(0, 0, 0),
                new Quaternionf(),
                new Vector3f(1, 1, 1)
        );
    }
}
