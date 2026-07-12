package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.shader.UShaderPass;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class UMaterialResolver {

    public UMaterialPassPlan resolve(UMaterialInstance instance, UMaterialStateContext context) {
        if (instance == null) {
            return UMaterialPassPlan.of(List.of());
        }

        UMaterialDefinition definition = instance.definition();
        List<UMaterialRenderData> passes = new ArrayList<>();

        for (UMaterialLayer layer : definition.layers()) {
            if (!layer.enabledByDefault()) {
                continue;
            }

            UMaterialParamSet resolvedParams = UMaterialRuntime.resolveParams(instance, layer, context);

            Set<UShaderFeature> mergedFeatures = EnumSet.noneOf(UShaderFeature.class);
            mergedFeatures.addAll(definition.globalFeatures());
            mergedFeatures.addAll(layer.features());

            UShaderPass shaderPass = resolveShaderPass(layer, mergedFeatures);
            int color = resolveColor(resolvedParams, layer, shaderPass);
            float alpha = resolveAlpha(resolvedParams, layer);
            float emissiveStrength = resolveEmissiveStrength(resolvedParams, layer);
            float uvScrollU = resolvedParams.getFloat(UMaterialParams.UV_SCROLL_U, 0.0f);
            float uvScrollV = resolvedParams.getFloat(UMaterialParams.UV_SCROLL_V, 0.0f);

            UMaterialRenderData.Builder builder = UMaterialRenderData.builder()
                    .layerName(layer.name())
                    .priority(layer.priority())
                    .renderMode(layer.renderMode())
                    .blendMode(layer.blendMode())
                    .shaderProfile(definition.shaderProfile())
                    .shaderPass(resolveShaderPass(layer, mergedFeatures))
                    .features(mergedFeatures)
                    .resolvedParams(resolvedParams)
                    .color(color)
                    .alpha(alpha)
                    .emissiveStrength(emissiveStrength)
                    .uvScrollU(uvScrollU)
                    .uvScrollV(uvScrollV)
                    .shaderProfile(definition.shaderProfile())
                    .shaderPass(shaderPass);

            for (var entry : layer.textures().entrySet()) {
                UTextureBinding binding = entry.getValue();
                if (binding != null) {
                    builder.texture(entry.getKey(), binding);
                }
            }

            passes.add(builder.build());
        }

        return UMaterialPassPlan.of(passes);
    }

    private static int resolveColor(UMaterialParamSet params, UMaterialLayer layer, UShaderPass shaderPass) {
        if (shaderPass == UShaderPass.OUTLINE) {
            return params.getColor(UMaterialParams.OUTLINE_COLOR, 0xFF000000);
        }
        return params.getColor(UMaterialParams.BASE_COLOR, layer.defaultTintColor());
    }

    private static float resolveAlpha(UMaterialParamSet params, UMaterialLayer layer) {
        return params.getFloat(UMaterialParams.ALPHA, layer.defaultAlpha());
    }

    private static float resolveEmissiveStrength(UMaterialParamSet params, UMaterialLayer layer) {
        return params.getFloat(UMaterialParams.EMISSIVE_STRENGTH, layer.defaultEmissiveStrength());
    }

    private static UShaderPass resolveShaderPass(UMaterialLayer layer, Set<UShaderFeature> features) {
        if (features.contains(UShaderFeature.OUTLINE)) {
            return UShaderPass.OUTLINE;
        }
        if (features.contains(UShaderFeature.EMISSIVE)) {
            return UShaderPass.EMISSIVE;
        }
        switch (layer.renderMode()) {
            case ADDITIVE:
                return UShaderPass.ADDITIVE;
            case TRANSLUCENT:
                return UShaderPass.TRANSLUCENT;
            case OVERLAY:
                return UShaderPass.OVERLAY;
            default:
                return UShaderPass.BASE;
        }
    }
}