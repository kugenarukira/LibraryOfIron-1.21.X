package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

public record PostureBreakPayload(UUID entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PostureBreakPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_posture_break"));

    public static final StreamCodec<ByteBuf, PostureBreakPayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, PostureBreakPayload::entityId,
                    PostureBreakPayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
