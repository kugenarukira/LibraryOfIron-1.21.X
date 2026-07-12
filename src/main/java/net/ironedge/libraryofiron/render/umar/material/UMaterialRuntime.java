package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.state.UMaterialDriver;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateRule;

public final class UMaterialRuntime {

    private UMaterialRuntime() {
    }

    public static UMaterialParamSet resolveParams(
            UMaterialInstance instance,
            UMaterialLayer layer,
            UMaterialStateContext context
    ) {
        UMaterialParamSet resolved = instance.definition().defaultParams();
        resolved.putAll(instance.overrides());
        resolved.putAll(layer.defaultParams());

        for (UMaterialDriver driver : layer.drivers()) {
            driver.apply(context, resolved);
        }

        for (UMaterialStateRule rule : layer.rules()) {
            if (rule.test(context)) {
                rule.apply(context, resolved);
            }
        }

        return resolved;
    }
}