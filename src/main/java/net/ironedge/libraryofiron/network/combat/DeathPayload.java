package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

public record DeathPayload(UUID entityId, UUID killerId, boolean isDead) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DeathPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_death"));

    public static final StreamCodec<ByteBuf, DeathPayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, DeathPayload::entityId,
                    PacketCodecs.UUID_CODEC, DeathPayload::killerId,
                    ByteBufCodecs.BOOL, DeathPayload::isDead,
                    DeathPayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
