package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

public record ParryPayload(UUID targetId, boolean success) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ParryPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_parry"));

    public static final StreamCodec<ByteBuf, ParryPayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, ParryPayload::targetId,
                    ByteBufCodecs.BOOL, ParryPayload::success,
                    ParryPayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
