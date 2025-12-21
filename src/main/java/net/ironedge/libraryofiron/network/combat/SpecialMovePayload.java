package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

public record SpecialMovePayload(UUID attackerId, UUID targetId, String moveId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpecialMovePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_special_move"));

    public static final StreamCodec<ByteBuf, SpecialMovePayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, SpecialMovePayload::attackerId,
                    PacketCodecs.UUID_CODEC, SpecialMovePayload::targetId,
                    ByteBufCodecs.STRING_UTF8, SpecialMovePayload::moveId,
                    SpecialMovePayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
