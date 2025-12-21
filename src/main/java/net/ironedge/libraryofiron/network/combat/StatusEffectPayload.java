package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

public record StatusEffectPayload(UUID entityId, String effectId, boolean added) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StatusEffectPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_status_effect"));

    public static final StreamCodec<ByteBuf, StatusEffectPayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, StatusEffectPayload::entityId,
                    ByteBufCodecs.STRING_UTF8, StatusEffectPayload::effectId,
                    ByteBufCodecs.BOOL, StatusEffectPayload::added,
                    StatusEffectPayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
