package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.combat.events.EntityStaggeredEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class PostureSystem {

    private static final CapabilityKey<PostureCap> POSTURE_KEY = new CapabilityKey<>("posture", PostureCap.class);

    public void applyPostureDamage(LivingEntity target, float amount) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null) return;

        PostureCap postureCap = null;
        if (container.has(POSTURE_KEY)) {
            postureCap = container.get(POSTURE_KEY).getData();
        }

        if (postureCap == null) return;

        float previous = postureCap.getCurrentPosture();
        postureCap.takeDamage(amount);

        if (postureCap.isStaggered()) {
            LoICore.context().getEventBus().fire(new EntityStaggeredEvent(target, previous));
        }
    }
}
