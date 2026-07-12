package net.ironedge.libraryofiron.render.umar.shader;

import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderData;

import java.util.Objects;

public final class UShaderBindingContext {

    private final UMaterialRenderData pass;

    public UShaderBindingContext(UMaterialRenderData pass) {
        this.pass = Objects.requireNonNull(pass, "pass");
    }

    public UMaterialRenderData pass() {
        return pass;
    }
}