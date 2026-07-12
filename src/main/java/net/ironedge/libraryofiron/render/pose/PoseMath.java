package net.ironedge.libraryofiron.render.pose;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class PoseMath {
    private PoseMath() {}

    public static PoseTransform compose(PoseTransform parent, PoseTransform local) {
        Quaternionf worldRot = new Quaternionf(parent.rotation()).mul(local.rotation());

        Vector3f worldScale = new Vector3f(
                parent.scale().x * local.scale().x,
                parent.scale().y * local.scale().y,
                parent.scale().z * local.scale().z
        );

        Vector3f scaledLocal = new Vector3f(local.translation()).mul(parent.scale());
        Vector3f rotatedLocal = scaledLocal.rotate(parent.rotation());
        Vector3f worldPos = new Vector3f(parent.translation()).add(rotatedLocal);

        return new PoseTransform(worldPos, worldRot, worldScale);
    }

    /** PoseTransform -> Matrix4f (TRS) */
    public static Matrix4f toMatrix(PoseTransform t) {
        return new Matrix4f()
                .translate(t.translation())
                .rotate(t.rotation())
                .scale(t.scale());
    }

    /** Matrix4f -> PoseTransform (extract TRS) */
    public static PoseTransform fromMatrix(Matrix4f m) {
        Vector3f pos = new Vector3f(m.m30(), m.m31(), m.m32());

        Quaternionf rot = new Quaternionf();
        m.getUnnormalizedRotation(rot);

        Vector3f scale = new Vector3f(1, 1, 1);
        m.getScale(scale);

        return new PoseTransform(pos, rot, scale);
    }

    /** Relative transform: a^-1 * b */
    public static PoseTransform relative(PoseTransform a, PoseTransform b) {
        Matrix4f A = toMatrix(a);
        Matrix4f B = toMatrix(b);

        Matrix4f invA = new Matrix4f(A).invert();
        Matrix4f rel = invA.mul(B);

        return fromMatrix(rel);
    }
}
