package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;

public final class UMaterialRenderUtil {

    private UMaterialRenderUtil() {
    }

    public static int colorA(int argb, float alphaMultiplier) {
        int a = (argb >> 24) & 0xFF;
        return clamp255((int) (a * alphaMultiplier));
    }

    public static int colorR(int argb) {
        return (argb >> 16) & 0xFF;
    }

    public static int colorG(int argb) {
        return (argb >> 8) & 0xFF;
    }

    public static int colorB(int argb) {
        return argb & 0xFF;
    }

    public static float resolveU(UMaterialRenderData pass, UTextureBinding binding, float baseU) {
        float u = baseU;
        if (binding != null) {
            u = u * binding.transform().uScale() + binding.transform().uOffset();
        }
        u += pass.uvScrollU();
        return u;
    }

    public static float resolveV(UMaterialRenderData pass, UTextureBinding binding, float baseV) {
        float v = baseV;
        if (binding != null) {
            v = v * binding.transform().vScale() + binding.transform().vOffset();
        }
        v += pass.uvScrollV();
        return v;
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }
}