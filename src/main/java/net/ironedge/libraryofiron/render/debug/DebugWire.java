package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class DebugWire {
    private DebugWire() {}

    // Draws an XZ square centered at origin (0,0,0)
    public static void squareAtOrigin(VertexConsumer vc, PoseStack ps, float r, int argb) {
        var pose = ps.last();

        float x0 = -r, x1 = r;
        float y = 0f;
        float z0 = -r, z1 = r;

        line(vc, pose, x0, y, z0, x1, y, z0, argb);
        line(vc, pose, x1, y, z0, x1, y, z1, argb);
        line(vc, pose, x1, y, z1, x0, y, z1, argb);
        line(vc, pose, x0, y, z1, x0, y, z0, argb);
    }

    private static void line(VertexConsumer vc, PoseStack.Pose pose,
                             float ax,float ay,float az, float bx,float by,float bz, int argb) {
        vc.addVertex(pose, ax, ay, az).setColor(argb).setNormal(0, 1, 0);
        vc.addVertex(pose, bx, by, bz).setColor(argb).setNormal(0, 1, 0);
    }
}
