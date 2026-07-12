package net.ironedge.libraryofiron.render.physics.provider;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class PhysicsSpline {
    private PhysicsSpline() {}

    public static List<Vector3f> catmullRom(List<Vector3f> in, int stepsPerSegment) {
        List<Vector3f> out = new ArrayList<>();
        if (in.size() < 2) return out;
        if (in.size() == 2) {
            out.addAll(in);
            return out;
        }

        for (int i = 0; i < in.size() - 1; i++) {
            Vector3f p0 = in.get(Math.max(0, i - 1));
            Vector3f p1 = in.get(i);
            Vector3f p2 = in.get(i + 1);
            Vector3f p3 = in.get(Math.min(in.size() - 1, i + 2));

            for (int s = 0; s < stepsPerSegment; s++) {
                float t = s / (float) stepsPerSegment;
                out.add(catmullRomPoint(p0, p1, p2, p3, t));
            }
        }

        out.add(new Vector3f(in.get(in.size() - 1)));
        return out;
    }

    private static Vector3f catmullRomPoint(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        return new Vector3f(
                0.5f * ((2f * p1.x) +
                        (-p0.x + p2.x) * t +
                        (2f * p0.x - 5f * p1.x + 4f * p2.x - p3.x) * t2 +
                        (-p0.x + 3f * p1.x - 3f * p2.x + p3.x) * t3),

                0.5f * ((2f * p1.y) +
                        (-p0.y + p2.y) * t +
                        (2f * p0.y - 5f * p1.y + 4f * p2.y - p3.y) * t2 +
                        (-p0.y + 3f * p1.y - 3f * p2.y + p3.y) * t3),

                0.5f * ((2f * p1.z) +
                        (-p0.z + p2.z) * t +
                        (2f * p0.z - 5f * p1.z + 4f * p2.z - p3.z) * t2 +
                        (-p0.z + 3f * p1.z - 3f * p2.z + p3.z) * t3)
        );
    }
}