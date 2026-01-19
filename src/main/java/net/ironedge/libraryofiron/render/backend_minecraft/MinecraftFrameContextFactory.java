package net.ironedge.libraryofiron.render.backend_minecraft;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MinecraftFrameContextFactory {

    private MinecraftFrameContextFactory() {}

    public static FrameContext build(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        // Stub for now: identity matrices until we wire real camera matrices
        Matrix4f view = new Matrix4f();
        Matrix4f proj = new Matrix4f();
        Vector3f camPos = new Vector3f(
                player != null ? (float) player.getX() : 0f,
                player != null ? (float) player.getY() : 0f,
                player != null ? (float) player.getZ() : 0f
        );

        FrameContext ctx = new FrameContext(partialTicks, view, proj, camPos);

        // Attach MC objects *without core importing Minecraft types*
        if (player != null) ctx.attach("player", player);
        if (mc.level != null) ctx.attach("level", mc.level);

        return ctx;
    }
}
