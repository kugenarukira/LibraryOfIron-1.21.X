package net.ironedge.libraryofiron.network;

import net.ironedge.libraryofiron.client.combat.CombatClientHandler;
import net.ironedge.libraryofiron.network.combat.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;

public class CombatNetworkRegistry {

    @SubscribeEvent
    public static void registerServer(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);

        registrar.playBidirectional(HitPayload.TYPE, HitPayload.STREAM_CODEC, CombatServerHandler::handleHit);
        registrar.playBidirectional(ParryPayload.TYPE, ParryPayload.STREAM_CODEC, CombatServerHandler::handleParry);
        registrar.playBidirectional(PostureBreakPayload.TYPE, PostureBreakPayload.STREAM_CODEC, CombatServerHandler::handlePostureBreak);
        registrar.playBidirectional(SpecialMovePayload.TYPE, SpecialMovePayload.STREAM_CODEC, CombatServerHandler::handleSpecialMove);
        registrar.playBidirectional(DeathPayload.TYPE, DeathPayload.STREAM_CODEC, CombatServerHandler::handleDeath);
        registrar.playBidirectional(StatusEffectPayload.TYPE, StatusEffectPayload.STREAM_CODEC, CombatServerHandler::handleStatusEffect);
    }

    @SubscribeEvent
    public static void registerClient(RegisterClientPayloadHandlersEvent event) {
        event.register(HitPayload.TYPE, CombatClientHandler::handleHit);
        event.register(ParryPayload.TYPE, CombatClientHandler::handleParry);
        event.register(PostureBreakPayload.TYPE, CombatClientHandler::handlePostureBreak);
        event.register(SpecialMovePayload.TYPE, CombatClientHandler::handleSpecialMove);
        event.register(DeathPayload.TYPE, CombatClientHandler::handleDeath);
        event.register(StatusEffectPayload.TYPE, CombatClientHandler::handleStatusEffect);
    }
}
