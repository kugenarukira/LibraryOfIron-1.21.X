package net.ironedge.libraryofiron.render.umar.material;

public final class UMaterialDebugBootstrap {

    private UMaterialDebugBootstrap() {
    }

    public static void init() {
        UMaterialDebugDefinitions.registerAll();
        UMaterialDebugInstances.init();
        UMaterialDebugSelfTest.run();
    }
}