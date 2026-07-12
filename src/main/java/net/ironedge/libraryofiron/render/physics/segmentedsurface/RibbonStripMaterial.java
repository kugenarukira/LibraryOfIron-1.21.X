package net.ironedge.libraryofiron.render.physics.segmentedsurface;

import net.ironedge.libraryofiron.render.umar.material.UMaterialCompat;
import net.ironedge.libraryofiron.render.umar.material.UMaterialInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class RibbonStripMaterial {

    private final UMaterialInstance material;

    public RibbonStripMaterial(UMaterialInstance material) {
        this.material = Objects.requireNonNull(material, "material");
    }

    public UMaterialInstance material() {
        return material;
    }

    public ResourceLocation texture() {
        return UMaterialCompat.baseTexture(material);
    }

    public boolean translucent() {
        return UMaterialCompat.isTranslucent(material);
    }

    public int baseColor() {
        return UMaterialCompat.baseColor(material);
    }

    public float alpha() {
        return UMaterialCompat.alpha(material);
    }
}