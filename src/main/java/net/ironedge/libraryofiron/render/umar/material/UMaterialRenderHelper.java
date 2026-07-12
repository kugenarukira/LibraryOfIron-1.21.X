package net.ironedge.libraryofiron.render.umar.material;

public final class UMaterialRenderHelper {

    private UMaterialRenderHelper() {}

    public static int[] rgba(UMaterialInstance mat) {
        int argb = UMaterialCompat.baseColor(mat);
        float alphaMul = UMaterialCompat.alpha(mat);

        int a = (int)(((argb >> 24) & 0xFF) * alphaMul);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        return new int[]{r, g, b, a};
    }
}