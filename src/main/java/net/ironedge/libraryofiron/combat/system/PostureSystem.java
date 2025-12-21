package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.capability.CapabilityContainer;
import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.combat.events.EntityStaggeredEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

public final class PostureSystem {

    public static final CapabilityKey<PostureCap> POSTURE_KEY = new CapabilityKey<>("posture", PostureCap.class);

    /** Apply posture damage. Returns true if staggered. */
    public static boolean applyPostureDamage(LivingEntity target, float amount) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(POSTURE_KEY)) return false;

        PostureCap cap = container.get(POSTURE_KEY).getData();
        if (cap == null) return false;

        float previous = cap.getCurrentPosture();
        cap.takeDamage(amount);

        boolean staggered = cap.isStaggered();
        if (staggered) {
            LoICore.context().getEventBus().fire(new EntityStaggeredEvent(target, previous));
        }

        return staggered;
    }

    /** Fully break posture */
    public static void applyPostureBreak(LivingEntity target) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(POSTURE_KEY)) return;

        PostureCap cap = container.get(POSTURE_KEY).getData();
        if (cap == null) return;

        cap.breakPosture();
        LoICore.context().getEventBus().fire(new EntityStaggeredEvent(target, cap.getCurrentPosture()));
    }

    /** Reset posture */
    public static void resetPosture(LivingEntity target) {
        CapabilityContainer container = LoICore.context().getCapabilityContainer(target);
        if (container == null || !container.has(POSTURE_KEY)) return;

        PostureCap cap = container.get(POSTURE_KEY).getData();
        if (cap != null) cap.resetPosture();
    }
}
