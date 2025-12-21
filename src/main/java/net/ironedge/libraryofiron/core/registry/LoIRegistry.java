package net.ironedge.libraryofiron.core.registry;

import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.HealthCap;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.capability.builtin.StaminaCap;
import net.ironedge.libraryofiron.capability.builtin.StatusEffectCap;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.core.LoILog;

public class LoIRegistry {

    public static CapabilityKey<PostureCap> POSTURE_CAP_KEY;
    public static CapabilityKey<HealthCap> HEALTH_CAP_KEY;
    public static CapabilityKey<StaminaCap> STAMINA_CAP_KEY;
    public static CapabilityKey<StatusEffectCap> STATUS_EFFECT_KEY;

    public static void registerAll() {
        POSTURE_CAP_KEY = new CapabilityKey<>("posture", PostureCap.class);
        HEALTH_CAP_KEY = new CapabilityKey<>("health", HealthCap.class);
        STAMINA_CAP_KEY = new CapabilityKey<>("stamina", StaminaCap.class);
        STATUS_EFFECT_KEY = new CapabilityKey<>("status_effects", StatusEffectCap.class);

        LoICore.context().registerData(POSTURE_CAP_KEY, new PostureCap());
        LoICore.context().registerData(HEALTH_CAP_KEY, new HealthCap(100));
        LoICore.context().registerData(STAMINA_CAP_KEY, new StaminaCap(100));
        LoICore.context().registerData(STATUS_EFFECT_KEY, new StatusEffectCap());

        LoILog.info("Registered core capabilities");
    }
}
