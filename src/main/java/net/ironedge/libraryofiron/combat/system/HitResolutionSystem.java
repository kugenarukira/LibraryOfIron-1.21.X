package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.EntityHitEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class HitResolutionSystem {

    private final ComboSystem comboSystem;
    private final BlockSystem blockSystem;
    private final ParrySystem parrySystem;

    public HitResolutionSystem(
            ComboSystem comboSystem,
            BlockSystem blockSystem,
            ParrySystem parrySystem
    ) {
        this.comboSystem = comboSystem;
        this.blockSystem = blockSystem;
        this.parrySystem = parrySystem;
    }

    public void resolveHit(LivingEntity attacker, LivingEntity target, HitProfile hit) {
        if (attacker == null || target == null || hit == null) return;
        if (target.isDeadOrDying()) return;

        /* ==================================================
         * 1️⃣ PARRY (perfect timing, consumes hit)
         * ================================================== */
        if (parrySystem.tryParry(attacker, target, hit)) {
            // Parry completely negates the hit
            comboSystem.resetCombo(attacker, hit.id());
            return;
        }

        /* ==================================================
         * 2️⃣ BLOCK (stamina-based mitigation)
         * ================================================== */
        if (blockSystem.tryBlock(attacker, target, hit)) {
            // Block stops damage but still counts as an interaction
            comboSystem.resetCombo(attacker, hit.id());
            return;
        }

        /* ==================================================
         * 3️⃣ HIT (nothing stopped it)
         * ================================================== */

        // Register combo
        comboSystem.registerHit(attacker, hit.id());

        // Fire hit event (damage/posture handled elsewhere)
        LoICore.context()
                .getEventBus()
                .fire(new EntityHitEvent(attacker, target, hit));

        /* ==================================================
         * 4️⃣ SPECIAL MOVE CHECK
         * ================================================== */
        if (comboSystem.hasReachedSpecialThreshold(attacker, hit.id())) {
            comboSystem.triggerSpecialMove(attacker, target, hit.id());
        }
    }

}