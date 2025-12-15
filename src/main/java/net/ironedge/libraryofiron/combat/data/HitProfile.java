package net.ironedge.libraryofiron.combat.data;

public record HitProfile(
        String id,
        float baseDamage,
        float staminaCost,
        float knockback,
        float postureDamage,
        DamageType damageType
) {}