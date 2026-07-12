package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerBlendMode;
import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.shader.UShaderProfile;
import net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers;
import net.ironedge.libraryofiron.render.umar.texture.UTextureAnimation;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.minecraft.resources.ResourceLocation;

public final class UMaterialDebugDefinitions {


    private static final String MODID = "libraryofiron";

    public static final ResourceLocation DEBUG_BASIC_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/basic");

    public static final ResourceLocation DEBUG_FLAT_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/flat");

    public static final ResourceLocation DEBUG_TINTED_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/tinted");

    public static final ResourceLocation DEBUG_EMISSIVE_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/emissive");

    public static final ResourceLocation DEBUG_VISOR_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/visor");

    public static final ResourceLocation DEBUG_OUTLINE_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "debug/outline");

    private UMaterialDebugDefinitions() {
    }

    public static void registerAll() {
        UMaterialRegistry.register(debugBasic());
        UMaterialRegistry.register(debugTinted());
        UMaterialRegistry.register(debugEmissive());
        UMaterialRegistry.register(debugVisor());
        UMaterialRegistry.register(debugOutline());
        UMaterialRegistry.register(debugFlat());
    }


    public static UMaterialDefinition debugBasic() {
        ResourceLocation baseTexture = tex("debug/basic");

        return UMaterialDefinition.builder(DEBUG_BASIC_ID)
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

    public static UMaterialDefinition debugFlat() {
        ResourceLocation baseTexture = tex("debug/flat");

        return UMaterialDefinition.builder(DEBUG_FLAT_ID)
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

    public static UMaterialDefinition debugTinted() {
        ResourceLocation baseTexture = tex("debug/tinted");

        UMaterialParamSet defaults = new UMaterialParamSet()
                .setColor(UMaterialParams.BASE_COLOR, 0xFFFFFFFF);

        return UMaterialDefinition.builder(DEBUG_TINTED_ID)
                .shaderProfile(UShaderProfile.LIT)
                .globalFeature(UShaderFeature.TINT)
                .defaultParams(defaults)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.OPAQUE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(baseTexture))
                                .feature(UShaderFeature.TINT)
                                .defaultTintColor(0xFFFFFFFF)
                                .build()
                )
                .build();
    }

    public static UMaterialDefinition debugEmissive() {
        ResourceLocation baseTexture = tex("debug/emissive_base");
        ResourceLocation emissiveTexture = tex("debug/emissive_glow");

        UMaterialParamSet defaults = new UMaterialParamSet()
                .setColor(UMaterialParams.EMISSIVE_COLOR, 0xFFFFFFFF)
                .setFloat(UMaterialParams.EMISSIVE_STRENGTH, 1.0f);

        return UMaterialDefinition.builder(DEBUG_EMISSIVE_ID)
                .shaderProfile(UShaderProfile.LIT)
                .globalFeature(UShaderFeature.EMISSIVE)
                .defaultParams(defaults)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.OPAQUE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(baseTexture))
                                .build()
                )
                .layer(
                        UMaterialLayer.builder("emissive")
                                .priority(10)
                                .renderMode(ULayerRenderMode.ADDITIVE)
                                .blendMode(ULayerBlendMode.ADD)
                                .texture(UMaterialTextureSlot.EMISSIVE, UTextureBinding.of(emissiveTexture))
                                .feature(UShaderFeature.EMISSIVE)
                                .defaultEmissiveStrength(1.0f)
                                .driver(
                                        net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers.pulse(
                                                UMaterialParams.EMISSIVE_STRENGTH,
                                                0.15f,
                                                0.5f,
                                                2.0f
                                        )
                                )
                                .rule(
                                        net.ironedge.libraryofiron.render.umar.state.UMaterialStateRule.builder(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialConditions.movementAbove(0.05f)
                                        ).driver(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers.movementBlend(
                                                        UMaterialParams.EMISSIVE_STRENGTH,
                                                        0.4f,
                                                        1.0f,
                                                        3.0f
                                                )
                                        ).build()
                                )
                                .build()
                )
                .build();
    }

    public static UMaterialDefinition debugVisor() {
        ResourceLocation visorBase = tex("debug/visor_base");
        ResourceLocation visorNoise = tex("debug/visor_noise");

        UMaterialParamSet defaults = new UMaterialParamSet()
                .setFloat(UMaterialParams.ALPHA, 0.5f)
                .setFloat(UMaterialParams.UV_SCROLL_U, 0.0f)
                .setFloat(UMaterialParams.UV_SCROLL_V, 0.01f)
                .setFloat(UMaterialParams.NOISE_STRENGTH, 1.0f);

        return UMaterialDefinition.builder(DEBUG_VISOR_ID)
                .shaderProfile(UShaderProfile.TRANSLUCENT)
                .globalFeature(UShaderFeature.UV_SCROLL)
                .globalFeature(UShaderFeature.OVERLAY)
                .defaultParams(defaults)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.TRANSLUCENT)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(visorBase))
                                .defaultAlpha(0.5f)
                                .rule(
                                        net.ironedge.libraryofiron.render.umar.state.UMaterialStateRule.builder(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialConditions.hurt()
                                        ).driver(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers.healthBlend(
                                                        UMaterialParams.ALPHA,
                                                        0.35f,
                                                        0.85f
                                                )
                                        ).build()
                                )
                                .build()
                )
                .layer(
                        UMaterialLayer.builder("noise")
                                .priority(10)
                                .renderMode(ULayerRenderMode.TRANSLUCENT)
                                .blendMode(ULayerBlendMode.SCREEN)
                                .texture(
                                        UMaterialTextureSlot.NOISE,
                                        UTextureBinding.builder(visorNoise)
                                                .animation(
                                                        UTextureAnimation.builder()
                                                                .scrollVPerTick(0.01f)
                                                                .build()
                                                )
                                                .build()
                                )
                                .feature(UShaderFeature.UV_SCROLL)
                                .feature(UShaderFeature.OVERLAY)
                                .defaultAlpha(0.65f)
                                .driver(
                                        net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers.scroll(
                                                UMaterialParams.UV_SCROLL_U,
                                                UMaterialParams.UV_SCROLL_V,
                                                0.0f,
                                                0.01f
                                        )
                                )
                                .rule(
                                        net.ironedge.libraryofiron.render.umar.state.UMaterialStateRule.builder(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialConditions.sprinting()
                                        ).driver(
                                                net.ironedge.libraryofiron.render.umar.state.UMaterialDrivers.movementBlend(
                                                        UMaterialParams.NOISE_STRENGTH,
                                                        0.5f,
                                                        1.0f,
                                                        2.5f
                                                )
                                        ).build()
                                )
                                .build()
                )
                .build();
    }

    public static UMaterialDefinition debugOutline() {
        ResourceLocation baseTexture = tex("debug/basic");

        UMaterialParamSet defaults = new UMaterialParamSet()
                .setColor(UMaterialParams.BASE_COLOR, 0xFFFFFFFF)
                .setColor(UMaterialParams.OUTLINE_COLOR, 0xFF000000)
                .setFloat(UMaterialParams.OUTLINE_WIDTH, 1.0f);

        return UMaterialDefinition.builder(DEBUG_OUTLINE_ID)
                .shaderProfile(UShaderProfile.LIT)
                .defaultParams(defaults)
                .layer(
                        UMaterialLayer.builder("base")
                                .priority(0)
                                .renderMode(ULayerRenderMode.OPAQUE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(baseTexture))
                                .build()
                )
                .layer(
                        UMaterialLayer.builder("outline")
                                .priority(100)
                                .renderMode(ULayerRenderMode.OUTLINE)
                                .blendMode(ULayerBlendMode.NORMAL)
                                .texture(UMaterialTextureSlot.BASE, UTextureBinding.of(baseTexture))
                                .feature(UShaderFeature.OUTLINE)
                                .defaultParams(
                                        new UMaterialParamSet()
                                                .setColor(UMaterialParams.OUTLINE_COLOR, 0xFF000000)
                                                .setFloat(UMaterialParams.OUTLINE_WIDTH, 1.0f)
                                )
                                .build()
                )
                .build();
    }

    private static ResourceLocation tex(String pathNoExt) {
        return UMaterialBuiltins.umatTexture(MODID, pathNoExt);
    }
}