package net.ironedge.libraryofiron.render.umar.state;

import net.ironedge.libraryofiron.render.umar.material.UMaterialParamSet;

public final class UMaterialDrivers {

    private UMaterialDrivers() {
    }

    public static UMaterialDriver pulse(String targetKey, float speed, float min, float max) {
        return (context, params) -> {
            float t = context.ageInTicks() + context.partialTick();
            float wave = (float) ((Math.sin(t * speed) + 1.0) * 0.5);
            float value = lerp(min, max, wave);
            params.setFloat(targetKey, value);
        };
    }

    public static UMaterialDriver scroll(String uKey, String vKey, float uPerTick, float vPerTick) {
        return (context, params) -> {
            float t = context.ageInTicks() + context.partialTick();
            params.setFloat(uKey, t * uPerTick);
            params.setFloat(vKey, t * vPerTick);
        };
    }

    public static UMaterialDriver healthBlend(String targetKey, float minValue, float maxValue) {
        return (context, params) -> {
            float missingHealth = 1.0f - context.healthPercent();
            float value = lerp(minValue, maxValue, missingHealth);
            params.setFloat(targetKey, value);
        };
    }

    public static UMaterialDriver movementBlend(String targetKey, float speedForMax, float minValue, float maxValue) {
        return (context, params) -> {
            float normalized = speedForMax <= 0.0f
                    ? 1.0f
                    : clamp01(context.movementSpeed() / speedForMax);

            float value = lerp(minValue, maxValue, normalized);
            params.setFloat(targetKey, value);
        };
    }

    public static UMaterialDriver multiplyFloat(String targetKey, float factor) {
        return (context, params) -> {
            float current = params.getFloat(targetKey, 0.0f);
            params.setFloat(targetKey, current * factor);
        };
    }

    public static UMaterialDriver addFloat(String targetKey, float amount) {
        return (context, params) -> {
            float current = params.getFloat(targetKey, 0.0f);
            params.setFloat(targetKey, current + amount);
        };
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}