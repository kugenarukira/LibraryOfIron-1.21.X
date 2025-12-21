package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.StanceCap;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class StanceSystem {

    public static final CapabilityKey<StanceCap> STANCE_KEY = new CapabilityKey<>("stance", StanceCap.class);

    public static StanceCap getCap(LivingEntity entity) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(entity);
        if (container == null || !container.has(STANCE_KEY)) return null;
        return container.get(STANCE_KEY).getData();
    }

    public static boolean switchStance(LivingEntity entity, String stanceId) {
        StanceCap cap = getCap(entity);
        if (cap == null) return false;
        return cap.switchStance(stanceId);
    }

    public static StanceCap.Stance getCurrentStance(LivingEntity entity) {
        StanceCap cap = getCap(entity);
        if (cap == null) return null;
        return cap.getCurrent();
    }

    public static float getDamageMultiplier(LivingEntity entity) {
        StanceCap.Stance stance = getCurrentStance(entity);
        return stance != null ? stance.getDamageMultiplier() : 1f;
    }

    public static float getPostureMultiplier(LivingEntity entity) {
        StanceCap.Stance stance = getCurrentStance(entity);
        return stance != null ? stance.getPostureMultiplier() : 1f;
    }
}
