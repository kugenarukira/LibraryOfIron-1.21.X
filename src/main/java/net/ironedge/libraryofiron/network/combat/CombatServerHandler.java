package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.combat.system.CombatSystem;
import net.ironedge.libraryofiron.combat.system.StanceSystem;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class CombatServerHandler {

    public static void handleHit(HitPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity attacker = CombatSystem.getEntityFromUUID(payload.attackerId());
            LivingEntity target = CombatSystem.getEntityFromUUID(payload.targetId());

            // Ensure stances are applied server-side
            StanceSystem.switchStance(attacker, payload.attackerStance());
            StanceSystem.switchStance(target, payload.targetStance());

            CombatSystem.applyHit(attacker, target, payload);
        });
    }


    public static void handleParry(ParryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity target = CombatSystem.getEntityFromUUID(payload.targetId());
            CombatSystem.applyParry(target, payload);
        });
    }

    public static void handlePostureBreak(PostureBreakPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity entity = CombatSystem.getEntityFromUUID(payload.entityId());
            CombatSystem.applyPostureBreak(entity, payload);
        });
    }

    public static void handleSpecialMove(SpecialMovePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity attacker = CombatSystem.getEntityFromUUID(payload.attackerId());
            LivingEntity target = CombatSystem.getEntityFromUUID(payload.targetId());
            CombatSystem.applySpecialMove(attacker, target, payload);
        });
    }

    public static void handleDeath(DeathPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity entity = CombatSystem.getEntityFromUUID(payload.entityId());
            CombatSystem.applyDeath(entity, payload);
        });
    }

    public static void handleStatusEffect(StatusEffectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LivingEntity entity = CombatSystem.getEntityFromUUID(payload.entityId());
            CombatSystem.applyStatusEffect(entity, payload);
        });
    }

}
