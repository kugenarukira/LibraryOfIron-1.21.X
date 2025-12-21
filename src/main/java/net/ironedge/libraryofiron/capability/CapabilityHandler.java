package net.ironedge.libraryofiron.capability;

import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.core.LoIContext;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Simple capability manager for entities.
 * Stores capabilities attached to entities.
 */
public final class CapabilityHandler {

    // WeakHashMap to automatically clean up entries when entities are garbage collected
    private static final Map<Entity, StatusEffectCap> statusEffectCaps = new WeakHashMap<>();

    /** Attach a StatusEffectCap to an entity if not already present */
    public static void attachStatusEffectCap(Entity entity) {
        statusEffectCaps.computeIfAbsent(entity, e -> new StatusEffectCap());
    }

    /** Get the StatusEffectCap of an entity, or null if not attached */
    public static StatusEffectCap getStatusEffectCap(Entity entity) {
        return statusEffectCaps.get(entity);
    }

    /** Remove the StatusEffectCap from an entity */
    public static void removeStatusEffectCap(Entity entity) {
        statusEffectCaps.remove(entity);
    }

    /** Tick all StatusEffectCaps (call from server tick loop) */
    public static void tickAll(LoIContext context) {
        for (Map.Entry<Entity, StatusEffectCap> entry : statusEffectCaps.entrySet()) {
            entry.getValue().tick(context, entry.getKey());
        }
    }
}
