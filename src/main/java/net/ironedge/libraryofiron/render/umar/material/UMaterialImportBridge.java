package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerBlendMode;
import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.shader.UShaderProfile;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.ironedge.libraryofiron.render.umr.importdata.UMRImportedMaterialDef;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class UMaterialImportBridge {

    private UMaterialImportBridge() {
    }

    public static Map<String, UMaterialInstance> buildImportedMaterialInstances(
            String namespace,
            String assetId,
            Map<String, UMRImportedMaterialDef> importedMaterials
    ) {
        Map<String, UMaterialInstance> out = new HashMap<>();

        if (importedMaterials == null || importedMaterials.isEmpty()) {
            return out;
        }

        for (var entry : importedMaterials.entrySet()) {
            String slot = entry.getKey();
            UMRImportedMaterialDef imported = entry.getValue();

            UMaterialDefinition definition = buildDefinition(
                    namespace,
                    assetId,
                    imported
            );

            UMaterialInstance instance = new UMaterialInstance(definition)
                    .setColor(UMaterialParams.BASE_COLOR, imported.baseColor())
                    .setFloat(UMaterialParams.ALPHA, imported.alpha());

            out.put(slot, instance);
        }

        return out;
    }

    private static UMaterialDefinition buildDefinition(
            String namespace,
            String assetId,
            UMRImportedMaterialDef imported
    ) {
        String safeSlot = sanitize(imported.slot());
        ResourceLocation materialId = ResourceLocation.fromNamespaceAndPath(
                namespace,
                "imported/" + sanitize(assetId) + "/" + safeSlot
        );

        UMaterialLayer.Builder layer = UMaterialLayer.builder("base")
                .priority(0)
                .renderMode(imported.alpha() < 0.999f ? ULayerRenderMode.TRANSLUCENT : ULayerRenderMode.OPAQUE)
                .blendMode(ULayerBlendMode.NORMAL)
                .defaultParams(
                        new UMaterialParamSet()
                                .setColor(UMaterialParams.BASE_COLOR, imported.baseColor())
                                .setFloat(UMaterialParams.ALPHA, imported.alpha())
                );

        if (imported.hasBaseTexture()) {
            layer.texture(
                    UMaterialTextureSlot.BASE,
                    UTextureBinding.of(imported.baseTexture())
            );
        }

        return UMaterialDefinition.builder(materialId)
                .shaderProfile(UShaderProfile.LIT)
                .defaultParams(
                        new UMaterialParamSet()
                                .setColor(UMaterialParams.BASE_COLOR, imported.baseColor())
                                .setFloat(UMaterialParams.ALPHA, imported.alpha())
                )
                .layer(layer.build())
                .build();
    }

    private static String sanitize(String in) {
        if (in == null || in.isBlank()) {
            return "unnamed";
        }

        return in.toLowerCase()
                .replace('\\', '/')
                .replaceAll("[^a-z0-9_./-]", "_")
                .replace("//", "/");
    }
}