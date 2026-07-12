package net.ironedge.libraryofiron.render.umar.material;

public final class UMaterialDebugInstances {

    public static UMaterialDefinition FLAT_WHITE;
    public static UMaterialDefinition BASIC_DEF;
    public static UMaterialDefinition TINTED_DEF;
    public static UMaterialDefinition EMISSIVE_DEF;
    public static UMaterialDefinition VISOR_DEF;
    public static UMaterialInstance BASIC_INSTANCE;
    public static UMaterialInstance TINTED_BLUE;
    public static UMaterialInstance EMISSIVE_HOT;
    public static UMaterialInstance VISOR_DARK;
    public static UMaterialDefinition OUTLINE_DEF;
    public static UMaterialInstance OUTLINE_BLACK;
    public static UMaterialInstance FLAT_WHITE_INSTANCE;

    private UMaterialDebugInstances() {
    }

    public static void init() {
        BASIC_DEF = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_BASIC_ID);
        TINTED_DEF = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_TINTED_ID);
        EMISSIVE_DEF = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_EMISSIVE_ID);
        VISOR_DEF = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_VISOR_ID);
        FLAT_WHITE = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_FLAT_ID);

        BASIC_INSTANCE = new UMaterialInstance(BASIC_DEF);

        FLAT_WHITE_INSTANCE = new UMaterialInstance(FLAT_WHITE);

        TINTED_BLUE = new UMaterialInstance(TINTED_DEF)
                .setColor(UMaterialParams.BASE_COLOR, 0xFF3399FF);

        EMISSIVE_HOT = new UMaterialInstance(EMISSIVE_DEF)
                .setFloat(UMaterialParams.EMISSIVE_STRENGTH, 2.5f);

        VISOR_DARK = new UMaterialInstance(VISOR_DEF)
                .setFloat(UMaterialParams.ALPHA, 0.35f);

        OUTLINE_DEF = UMaterialRegistry.require(UMaterialDebugDefinitions.DEBUG_OUTLINE_ID);

        OUTLINE_BLACK = new UMaterialInstance(OUTLINE_DEF)
                .setColor(UMaterialParams.BASE_COLOR, 0xFFFFFFFF)
                .setColor(UMaterialParams.OUTLINE_COLOR, 0xFF000000)
                .setFloat(UMaterialParams.OUTLINE_WIDTH, 1.0f);

    }
}