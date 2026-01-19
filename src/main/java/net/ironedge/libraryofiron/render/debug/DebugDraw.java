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
}
