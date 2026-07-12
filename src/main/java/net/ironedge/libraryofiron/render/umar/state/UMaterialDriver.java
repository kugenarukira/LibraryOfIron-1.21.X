package net.ironedge.libraryofiron.render.umar.state;

import net.ironedge.libraryofiron.render.umar.material.UMaterialParamSet;

@FunctionalInterface
public interface UMaterialDriver {
    void apply(UMaterialStateContext context, UMaterialParamSet params);
}