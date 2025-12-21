package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.ParryCap;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.capability.builtin.StaminaCap;
import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.SpecialMoveEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.event.events.PostureBreakEvent;
import net.minecraft.world.entity.LivingEntity;

public final class ParrySystem {

    private static final CapabilityKey<ParryCap> PARRY_KEY =
            new CapabilityKey<>("parry", ParryCap.class);

    private static final CapabilityKey<StaminaCap> STAMINA_KEY =
            new CapabilityKey<>("stamina", StaminaCap.class);

    private static final CapabilityKey<PostureCap> POSTURE_KEY =
            new CapabilityKey<>("posture", PostureCap.class);

    /**
     * Attempt a timing-based parry.
     * Applies stamina refund and attacker posture damage on success.
     */
    public boolean tryParry(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        CapabilityContainer targetContainer = LoICore.context().getCapabilityContainer(target);
        if (targetContainer == null || !targetContainer.has(PARRY_KEY)) return false;

        ParryCap parry = targetContainer.get(PARRY_KEY).getData();
        if (parry == null || !parry.isParryActive()) return false;

        if (!parry.consumeParry()) return false;

        // Refund some stamina to the defender
        if (targetContainer.has(STAMINA_KEY)) {
            StaminaCap stamina = targetContainer.get(STAMINA_KEY).getData();
            if (stamina != null) stamina.add(hit.parryStaminaRefund());
        }

        // Damage attacker's posture on successful parry
        CapabilityContainer attackerContainer = LoICore.context().getCapabilityContainer(attacker);
        if (attackerContainer != null && attackerContainer.has(POSTURE_KEY)) {
            PostureCap atkPosture = attackerContainer.get(POSTURE_KEY).getData();
            if (atkPosture != null) {
                atkPosture.takeDamage(hit.parryPostureDamage());
                if (atkPosture.isBroken()) {
                    LoICore.context().getEventBus().fire(new PostureBreakEvent(attacker));
                }
            }
        }

        // Fire special move event for counterattack
        LoICore.context().getEventBus().fire(new SpecialMoveEvent(target, "PARRY_COUNTER", attacker));
        return true;
    }
}
