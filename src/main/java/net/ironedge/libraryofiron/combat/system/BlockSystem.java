package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.capability.builtin.StaminaCap;
import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.EntityBlockEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.event.events.PostureBreakEvent;
import net.minecraft.world.entity.LivingEntity;

public final class BlockSystem {

    public static final CapabilityKey<StaminaCap> STAMINA_KEY =
            new CapabilityKey<>("stamina", StaminaCap.class);

    public static final CapabilityKey<PostureCap> POSTURE_KEY =
            new CapabilityKey<>("posture", PostureCap.class);

    /**
     * Attempt to block an incoming hit.
     * @return true if block succeeds
     */
    public boolean tryBlock(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null) return false;

        // Apply posture damage on block
        if (container.has(POSTURE_KEY)) {
            PostureCap posture = container.get(POSTURE_KEY).getData();
            if (posture != null) {
                posture.takeDamage(hit.blockPostureDamage());
                if (posture.isBroken()) {
                    LoICore.context().getEventBus().fire(new PostureBreakEvent(target));
                }
            }
        }

        // Fail if no stamina or not enough stamina
        if (!container.has(STAMINA_KEY)) return false;
        StaminaCap stamina = container.get(STAMINA_KEY).getData();
        if (stamina == null || !stamina.tryConsume(hit.blockStaminaCost())) return false;

        // Successful block
        LoICore.context().getEventBus().fire(new EntityBlockEvent(attacker, target, hit));
        return true;
    }
}
