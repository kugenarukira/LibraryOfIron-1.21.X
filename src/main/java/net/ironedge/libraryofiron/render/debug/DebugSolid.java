package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public final class DebugSolid {

    private DebugSolid() {}

    /** Convenience overload if you ever want to call it without a consumer (no-op for now). */
    public static void cube(Vector3f c, float size, int argb) {
        // This overload is intentionally a no-op in the VertexConsumer pipeline.
        // Keep it only if other code references it.
    }

    /**
     * Emit a solid cube as 12 triangles into a VertexConsumer.
     * Assumes PoseStack has already been translated into camera-relative space.
     */
    public static void cube(VertexConsumer vc, PoseStack ps, Vector3f c, float size, int argb) {
        float r = size * 0.5f;

        float x0 = c.x - r;
        float x1 = c.x + r;
        float y0 = c.y - r;
        float y1 = c.y + r;
        float z0 = c.z - r;
        float z1 = c.z + r;

        PoseStack.Pose pose = ps.last();

        // +X
        tri(vc, pose, x1,y0,z0, x1,y0,z1, x1,y1,z1, argb,  1,0,0);
        tri(vc, pose, x1,y0,z0, x1,y1,z1, x1,y1,z0, argb,  1,0,0);

        // -X
        tri(vc, pose, x0,y0,z1, x0,y0,z0, x0,y1,z0, argb, -1,0,0);
        tri(vc, pose, x0,y0,z1, x0,y1,z0, x0,y1,z1, argb, -1,0,0);

        // +Y
        tri(vc, pose, x0,y1,z0, x1,y1,z0, x1,y1,z1, argb,  0,1,0);
        tri(vc, pose, x0,y1,z0, x1,y1,z1, x0,y1,z1, argb,  0,1,0);

        // -Y
        tri(vc, pose, x0,y0,z1, x1,y0,z1, x1,y0,z0, argb,  0,-1,0);
        tri(vc, pose, x0,y0,z1, x1,y0,z0, x0,y0,z0, argb,  0,-1,0);

        // +Z
        tri(vc, pose, x1,y0,z1, x0,y0,z1, x0,y1,z1, argb,  0,0,1);
        tri(vc, pose, x1,y0,z1, x0,y1,z1, x1,y1,z1, argb,  0,0,1);

        // -Z
        tri(vc, pose, x0,y0,z0, x1,y0,z0, x1,y1,z0, argb,  0,0,-1);
        tri(vc, pose, x0,y0,z0, x1,y1,z0, x0,y1,z0, argb,  0,0,-1);
    }

    private static void tri(
            VertexConsumer vc,
            PoseStack.Pose pose,
            float ax,float ay,float az,
            float bx,float by,float bz,
            float cx,float cy,float cz,
            int argb,
            float nx,float ny,float nz
    ) {
        vc.addVertex(pose, ax,ay,az).setColor(argb).setNormal(nx,ny,nz);
        vc.addVertex(pose, bx,by,bz).setColor(argb).setNormal(nx,ny,nz);
        vc.addVertex(pose, cx,cy,cz).setColor(argb).setNormal(nx,ny,nz);
    }
}
