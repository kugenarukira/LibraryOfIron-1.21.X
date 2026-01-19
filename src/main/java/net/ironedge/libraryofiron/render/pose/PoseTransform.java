package net.ironedge.libraryofiron.render.pose;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public record PoseTransform(Vector3f translation, Quaternionf rotation, Vector3f scale) {
    public static PoseTransform identity() {
        return new PoseTransform(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1));
    }
}
