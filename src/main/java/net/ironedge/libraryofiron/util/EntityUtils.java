package net.ironedge.libraryofiron.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public final class EntityUtils {

    /** Fetch LivingEntity by UUID from the server */
    public static LivingEntity getEntity(ServerLevel level, UUID uuid) {
        return (LivingEntity) level.getEntity(uuid);
    }
}
