package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.HealthCap;
import net.ironedge.libraryofiron.combat.events.DamageDealtEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class DamageSystem {

    private static final CapabilityKey<HealthCap> HEALTH_KEY = new CapabilityKey<>("health", HealthCap.class);

    public void applyDamage(LivingEntity attacker, LivingEntity target, float damage) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null) return;

        HealthCap healthCap = null;
        if (container.has(HEALTH_KEY)) {
            healthCap = container.get(HEALTH_KEY).getData();
        }

        if (healthCap != null) {
            healthCap.damage(damage);
        }

        LoICore.context().getEventBus().fire(new DamageDealtEvent(attacker, target, damage));
    }
}
