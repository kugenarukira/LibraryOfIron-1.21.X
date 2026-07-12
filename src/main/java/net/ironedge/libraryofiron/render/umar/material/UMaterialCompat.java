package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.minecraft.resources.ResourceLocation;

public final class UMaterialCompat {

    private UMaterialCompat() {
    }

    public static ResourceLocation baseTexture(UMaterialDefinition material) {
        if (material == null) return null;

        for (UMaterialLayer layer : material.layers()) {
            UTextureBinding binding = layer.texture(UMaterialTextureSlot.BASE);
            if (binding != null) return binding.texture();
        }

        for (UMaterialLayer layer : material.layers()) {
            for (UTextureBinding binding : layer.textures().values()) {
                if (binding != null) return binding.texture();
            }
        }

        return null;
    }

    public static ResourceLocation baseTexture(UMaterialInstance material) {
        if (material == null) return null;
        return baseTexture(material.definition());
    }

    public static boolean isTranslucent(UMaterialDefinition material) {
        if (material == null) return false;

        return material.layers().stream().anyMatch(layer ->
                layer.renderMode() == ULayerRenderMode.TRANSLUCENT
        );
    }

    public static boolean isTranslucent(UMaterialInstance material) {
        if (material == null) return false;
        return isTranslucent(material.definition());
    }

    public static int baseColor(UMaterialInstance material) {
        if (material == null) return 0xFFFFFFFF;

        UMaterialParamSet merged = material.definition().defaultParams();
        merged.putAll(material.overrides());
        return merged.getColor(UMaterialParams.BASE_COLOR, 0xFFFFFFFF);
    }

    public static float alpha(UMaterialInstance material) {
        if (material == null) return 1.0f;

        UMaterialParamSet merged = material.definition().defaultParams();
        merged.putAll(material.overrides());
        return merged.getFloat(UMaterialParams.ALPHA, 1.0f);
    }

    public static float emissiveStrength(UMaterialInstance material) {
        if (material == null) return 0.0f;

        UMaterialParamSet merged = material.definition().defaultParams();
        merged.putAll(material.overrides());
        return merged.getFloat(UMaterialParams.EMISSIVE_STRENGTH, 0.0f);
    }
}