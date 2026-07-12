package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerBlendMode;
import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.shader.UShaderProfile;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.minecraft.resources.ResourceLocation;

public final class UMaterialBuiltins {

    private UMaterialBuiltins() {
    }

    public static ResourceLocation umatTexture(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "textures/umat/" + path + ".png");
    }

    public static UMaterialDefinition simple(ResourceLocation materialId, ResourceLocation baseTexture) {
        return UMaterialDefinition.builder(materialId)
                .shaderProfile(UShaderProfile.LIT)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.OPAQUE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(baseTexture))
                                .build()
                )
                .build();
    }

    public static UMaterialDefinition simpleGeo(ResourceLocation geoNameNoExt) {
        ResourceLocation texture = umatTexture(
                geoNameNoExt.getNamespace(),
                geoNameNoExt.getPath()
        );

        ResourceLocation materialId = ResourceLocation.fromNamespaceAndPath(
                geoNameNoExt.getNamespace(),
                "material/" + geoNameNoExt.getPath()
        );

        return simple(materialId, texture);
    }

    public static UMaterialDefinition outlinedGeo(
            ResourceLocation geoNameNoExt,
            int outlineColor,
            float outlineWidth
    ) {
        ResourceLocation texture = umatTexture(
                geoNameNoExt.getNamespace(),
                geoNameNoExt.getPath()
        );

        ResourceLocation materialId = ResourceLocation.fromNamespaceAndPath(
                geoNameNoExt.getNamespace(),
                "material/" + geoNameNoExt.getPath() + "_outline"
        );

        UMaterialParamSet defaults = new UMaterialParamSet()
                .setColor(UMaterialParams.BASE_COLOR, 0xFFFFFFFF)
                .setColor(UMaterialParams.OUTLINE_COLOR, outlineColor)
                .setFloat(UMaterialParams.OUTLINE_WIDTH, outlineWidth);

        return UMaterialDefinition.builder(materialId)
                .shaderProfile(UShaderProfile.LIT)
                .defaultParams(defaults)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.OPAQUE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(texture))
                                .build()
                )
                .layer(
                        UMaterialLayer.builder("outline")
                                .priority(100)
                                .renderMode(ULayerRenderMode.OUTLINE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(texture))
                                .feature(UShaderFeature.OUTLINE)
                                .defaultParams(
                                        new UMaterialParamSet()
                                                .setColor(UMaterialParams.OUTLINE_COLOR, outlineColor)
                                                .setFloat(UMaterialParams.OUTLINE_WIDTH, outlineWidth)
                                )
                                .build()
                )
                .build();
    }
}