package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.shader.UShaderBindingContext;
import net.ironedge.libraryofiron.render.umar.shader.UShaderBindings;
import net.ironedge.libraryofiron.render.umar.shader.UShaderPass;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class UMaterialRenderTypes {

    private UMaterialRenderTypes() {
    }

    public static RenderType forPass(UMaterialRenderData pass, ResourceLocation texture) {
        if (texture == null) {
            return null;
        }

        return UShaderBindings.resolveDefault(new UShaderBindingContext(pass), texture);
    }

    public static boolean isFullBright(UMaterialRenderData pass) {
        return pass.shaderPass() == UShaderPass.EMISSIVE
                || pass.emissiveStrength() > 0.001f;
    }

    private static final Function<ResourceLocation, RenderType> OUTLINE_SHELL = Util.memoize(UMaterialRenderTypes::createOutlineShell);

    public static RenderType outlineShell(ResourceLocation texture) {
        return OUTLINE_SHELL.apply(texture);
    }

    private static RenderType createOutlineShell(ResourceLocation texture) {
        var state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false))
                .setLightmapState(RenderType.LIGHTMAP)
                .setOverlayState(RenderType.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "umr_outline_shell",
                256,
                true,
                false,
                RenderPipelines.ENTITY_CUTOUT,
                state
        );
    }
}