package net.ironedge.libraryofiron.combat.data;

import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import java.util.List;

public record HitProfile(
        String id,
        float baseDamage,
        float staminaCost,
        float knockback,
        float postureDamage,
        DamageType damageType,
        float blockStaminaCost,
        float blockPostureDamage,
        int parryWindowTicks,
        int specialComboThreshold,
        float parryStaminaRefund,
        float parryPostureDamage,
        List<StatusEffectCap.StatusEffect> statusEffects // << add this
) {}
