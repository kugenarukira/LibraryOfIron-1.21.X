package net.ironedge.libraryofiron.render.umr.geo;

import net.minecraft.resources.ResourceLocation;

public final class GeoAssets {
    private GeoAssets() {}

    public static ResourceLocation geoJson(String modid, String name) {
        return ResourceLocation.fromNamespaceAndPath(modid, "models/geo/" + name + ".geo.json");
    }

    public static ResourceLocation geoTexture(String modid, String name) {
        return ResourceLocation.fromNamespaceAndPath(modid, "textures/umat/" + name + ".png");
    }
}