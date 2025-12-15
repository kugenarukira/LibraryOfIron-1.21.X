package net.ironedge.libraryofiron.combat.data;

import java.util.Map;

public record ArmorProfile(
        String id,
        Map<DamageType, Float> damageModifiers // 1.0 = full damage, 0.0 = immune
) {}