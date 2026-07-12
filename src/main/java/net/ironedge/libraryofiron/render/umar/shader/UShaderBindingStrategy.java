package net.ironedge.libraryofiron.render.umar.shader;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface UShaderBindingStrategy {
    RenderType resolveRenderType(UShaderBindingContext context, ResourceLocation texture);
}