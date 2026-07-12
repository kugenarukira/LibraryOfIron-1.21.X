package net.ironedge.libraryofiron.render.debug;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.core.RenderPerspectiveMode;
import net.ironedge.libraryofiron.render.debug.rigs.DebugMBodyRig;
import net.ironedge.libraryofiron.render.umar.material.UMaterialBuiltins;
import net.ironedge.libraryofiron.render.umar.material.UMaterialInstance;
import net.minecraft.resources.ResourceLocation;

public final class GeoDebugContent {
    private GeoDebugContent() {}

    public static void install(RenderEngine engine) {
        var modid = "libraryofiron";
        ResourceLocation geo = ResourceLocation.fromNamespaceAndPath(modid, "models/geo/debugmbody.geo.json");

        var mat = new UMaterialInstance(
                UMaterialBuiltins.outlinedGeo(
                        ResourceLocation.fromNamespaceAndPath(modid, "debugmbody"),
                        0xFF000000,
                        0.05f
                )
        );

        engine.graph().addNode(new GeoDebugRenderNode(
                geo,
                mat,
                new DebugMBodyRig(),
                RenderPerspectiveMode.THIRD_PERSON_ONLY
        ));
    }
}