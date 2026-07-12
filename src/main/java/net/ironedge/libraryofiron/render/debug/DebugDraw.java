package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public final class DebugDraw {

    private DebugDraw() {}

    public static void cross(VertexConsumer vc, PoseStack ps, Vector3f p, float r, int argb) {
        var pose = ps.last();

        vc.addVertex(pose, p.x - r, p.y, p.z).setColor(argb).setNormal(1f, 0f, 0f);
        vc.addVertex(pose, p.x + r, p.y, p.z).setColor(argb).setNormal(1f, 0f, 0f);

        vc.addVertex(pose, p.x, p.y - r, p.z).setColor(argb).setNormal(0f, 1f, 0f);
        vc.addVertex(pose, p.x, p.y + r, p.z).setColor(argb).setNormal(0f, 1f, 0f);

        vc.addVertex(pose, p.x, p.y, p.z - r).setColor(argb).setNormal(0f, 0f, 1f);
        vc.addVertex(pose, p.x, p.y, p.z + r).setColor(argb).setNormal(0f, 0f, 1f);
    }

    public static void line(VertexConsumer vc, PoseStack ps, Vector3f a, Vector3f b, int color) {
        float ar = ((color >> 16) & 0xFF) / 255f;
        float ag = ((color >> 8) & 0xFF) / 255f;
        float ab = (color & 0xFF) / 255f;
        float aa = ((color >>> 24) & 0xFF) / 255f;

        PoseStack.Pose pose = ps.last();

        Vector3f n = new Vector3f(b).sub(a);
        if (n.lengthSquared() > 1.0e-6f) n.normalize();
        else n.set(0, 1, 0);

        vc.addVertex(pose.pose(), a.x, a.y, a.z)
                .setColor(ar, ag, ab, aa)
                .setNormal(pose, n.x, n.y, n.z);

        vc.addVertex(pose.pose(), b.x, b.y, b.z)
                .setColor(ar, ag, ab, aa)
                .setNormal(pose, n.x, n.y, n.z);
    }
    public static void circleXY(VertexConsumer vc, PoseStack ps, Vector3f center, float radius, int color, int segments) {
        if (segments < 3) segments = 3;

        for (int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / segments);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / segments);

            Vector3f p0 = new Vector3f(
                    center.x + (float) Math.cos(a0) * radius,
                    center.y + (float) Math.sin(a0) * radius,
                    center.z
            );

            Vector3f p1 = new Vector3f(
                    center.x + (float) Math.cos(a1) * radius,
                    center.y + (float) Math.sin(a1) * radius,
                    center.z
            );

            line(vc, ps, p0, p1, color);
        }
    }

    public static void circleXZ(VertexConsumer vc, PoseStack ps, Vector3f center, float radius, int color, int segments) {
        if (segments < 3) segments = 3;

        for (int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / segments);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / segments);

            Vector3f p0 = new Vector3f(
                    center.x + (float) Math.cos(a0) * radius,
                    center.y,
                    center.z + (float) Math.sin(a0) * radius
            );

            Vector3f p1 = new Vector3f(
                    center.x + (float) Math.cos(a1) * radius,
                    center.y,
                    center.z + (float) Math.sin(a1) * radius
            );

            line(vc, ps, p0, p1, color);
        }
    }

    public static void circleYZ(VertexConsumer vc, PoseStack ps, Vector3f center, float radius, int color, int segments) {
        if (segments < 3) segments = 3;

        for (int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / segments);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / segments);

            Vector3f p0 = new Vector3f(
                    center.x,
                    center.y + (float) Math.cos(a0) * radius,
                    center.z + (float) Math.sin(a0) * radius
            );

            Vector3f p1 = new Vector3f(
                    center.x,
                    center.y + (float) Math.cos(a1) * radius,
                    center.z + (float) Math.sin(a1) * radius
            );

            line(vc, ps, p0, p1, color);
        }
    }
}
