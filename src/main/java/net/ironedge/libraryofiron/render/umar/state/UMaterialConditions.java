package net.ironedge.libraryofiron.render.umar.state;

public final class UMaterialConditions {

    private UMaterialConditions() {
    }

    public static UMaterialCondition always() {
        return context -> true;
    }

    public static UMaterialCondition hurt() {
        return UMaterialStateContext::hurt;
    }

    public static UMaterialCondition sprinting() {
        return UMaterialStateContext::sprinting;
    }

    public static UMaterialCondition airborne() {
        return UMaterialStateContext::airborne;
    }

    public static UMaterialCondition glowing() {
        return UMaterialStateContext::glowing;
    }

    public static UMaterialCondition healthBelow(float threshold) {
        return context -> context.healthPercent() < threshold;
    }

    public static UMaterialCondition powerAbove(float threshold) {
        return context -> context.powerLevel() > threshold;
    }

    public static UMaterialCondition movementAbove(float threshold) {
        return context -> context.movementSpeed() > threshold;
    }
}