package net.ironedge.libraryofiron.render.core;

import net.minecraft.client.Minecraft;

public final class PerspectiveUtil {
    private PerspectiveUtil() {}

    public static boolean isFirstPerson() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.options.getCameraType().isFirstPerson();
    }
}