package net.ironedge.libraryofiron.network.combat;

import net.ironedge.libraryofiron.network.PacketCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record HitPayload(
        UUID attackerId,
        UUID targetId,
        float damage,
        float postureDamage,
        boolean wasBlocked,
        boolean wasParried,
        List<String> statusEffects,
        String attackerStance,  // <-- added
        String targetStance     // <-- added
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HitPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("libraryofiron", "combat_hit"));

    public static final StreamCodec<ByteBuf, List<String>> STRING_LIST_CODEC = StreamCodec.of(
            (buf, list) -> {
                buf.writeInt(list.size());
                for (String s : list) ByteBufCodecs.STRING_UTF8.encode(buf, s);
            },
            buf -> {
                int size = buf.readInt();
                List<String> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) list.add(ByteBufCodecs.STRING_UTF8.decode(buf));
                return list;
            }
    );

    public static final StreamCodec<ByteBuf, HitPayload> STREAM_CODEC =
            StreamCodec.composite(
                    PacketCodecs.UUID_CODEC, HitPayload::attackerId,
                    PacketCodecs.UUID_CODEC, HitPayload::targetId,
                    ByteBufCodecs.FLOAT, HitPayload::damage,
                    ByteBufCodecs.FLOAT, HitPayload::postureDamage,
                    ByteBufCodecs.BOOL, HitPayload::wasBlocked,
                    ByteBufCodecs.BOOL, HitPayload::wasParried,
                    STRING_LIST_CODEC, HitPayload::statusEffects,
                    ByteBufCodecs.STRING_UTF8, HitPayload::attackerStance,
                    ByteBufCodecs.STRING_UTF8, HitPayload::targetStance,
                    HitPayload::new
            );

    @Override
    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
