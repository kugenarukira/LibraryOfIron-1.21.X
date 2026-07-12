package net.ironedge.libraryofiron.render.umar.shader;

import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderData;
import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class UShaderBindings {

    private UShaderBindings() {
    }

    public static RenderType resolveDefault(UShaderBindingContext context, ResourceLocation texture) {
        if (texture == null) {
            return null;
        }

        UMaterialRenderData pass = context.pass();

        return switch (pass.shaderPass()) {
            case BASE -> RenderType.entityCutoutNoCull(texture);
            case EMISSIVE -> RenderType.entityTranslucent(texture);
            case OVERLAY -> RenderType.entityTranslucent(texture);
            case TRANSLUCENT -> RenderType.entityTranslucent(texture);
            case ADDITIVE -> RenderType.entityTranslucent(texture);
            case OUTLINE -> UMaterialRenderTypes.outlineShell(texture);
            case CUSTOM -> RenderType.entityTranslucent(texture);
        };
    }
}