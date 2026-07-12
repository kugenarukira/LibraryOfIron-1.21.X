package net.ironedge.libraryofiron.render.umr.importdata;

import net.minecraft.resources.ResourceLocation;

public final class UMRImportedMaterialDef {

    private final String slot;
    private final String name;
    private final ResourceLocation baseTexture;
    private final int baseColor;
    private final float alpha;

    public UMRImportedMaterialDef(
            String slot,
            String name,
            ResourceLocation baseTexture,
            int baseColor,
            float alpha
    ) {
        this.slot = slot;
        this.name = name;
        this.baseTexture = baseTexture;
        this.baseColor = baseColor;
        this.alpha = alpha;
    }

    public String slot() {
        return slot;
    }

    public String name() {
        return name;
    }

    public ResourceLocation baseTexture() {
        return baseTexture;
    }

    public boolean hasBaseTexture() {
        return baseTexture != null;
    }

    public int baseColor() {
        return baseColor;
    }

    public float alpha() {
        return alpha;
    }
}