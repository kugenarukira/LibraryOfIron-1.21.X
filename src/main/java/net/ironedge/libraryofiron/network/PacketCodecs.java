package net.ironedge.libraryofiron.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public class PacketCodecs {
    public static final StreamCodec<ByteBuf, UUID> UUID_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.LONG, UUID::getMostSignificantBits,
                    ByteBufCodecs.LONG, UUID::getLeastSignificantBits,
                    (most, least) -> new UUID(most, least)
            );
}
