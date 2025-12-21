package net.ironedge.libraryofiron.client.combat;


import net.ironedge.libraryofiron.combat.system.CombatSystem;
import net.ironedge.libraryofiron.combat.system.StanceSystem;
import net.ironedge.libraryofiron.network.combat.*;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CombatClientHandler {

    public static void handleHit(HitPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Show attack VFX with correct stances
            CombatClientVisuals.showHit(payload);

            // Optionally apply stances client-side for animations
            LivingEntity attacker = CombatSystem.getEntityFromUUID(payload.attackerId());
            LivingEntity target = CombatSystem.getEntityFromUUID(payload.targetId());

            if (attacker != null) StanceSystem.switchStance(attacker, payload.attackerStance());
            if (target != null) StanceSystem.switchStance(target, payload.targetStance());
        });
    }


    public static void handleParry(ParryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            CombatClientVisuals.showParry(payload);
        });
    }

    public static void handlePostureBreak(PostureBreakPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            CombatClientVisuals.showPostureBreak(payload);
        });
    }

    public static void handleSpecialMove(SpecialMovePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            CombatClientVisuals.showSpecialMove(payload);
        });
    }

    public static void handleDeath(DeathPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            CombatClientVisuals.showDeath(payload);
        });
    }

    public static void handleStatusEffect(StatusEffectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            CombatClientVisuals.showStatusEffect(payload);
        });
    }
}