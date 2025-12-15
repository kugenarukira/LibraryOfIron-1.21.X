package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.data.HitProfile;
import net.ironedge.libraryofiron.combat.events.EntityBlockEvent;
import net.ironedge.libraryofiron.combat.events.EntityHitEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class HitResolutionSystem {

    public void resolveHit(LivingEntity attacker, LivingEntity target, HitProfile hitProfile) {
        // TODO: Replace with proper hitbox / collision detection
        boolean blocked = false; // placeholder, replace with shield/stamina check

        if (blocked) {
            LoICore.context().getEventBus().fire(new EntityBlockEvent(attacker, target, hitProfile));
            return;
        }

        LoICore.context().getEventBus().fire(new EntityHitEvent(attacker, target, hitProfile));
    }
}