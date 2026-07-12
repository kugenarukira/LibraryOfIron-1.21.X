package net.ironedge.libraryofiron.core.registry;

import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.*;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.core.LoILog;
import net.ironedge.libraryofiron.render.anchor.AnchorType;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolverRegistry;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.resolve.PoseGraphAnchorProvider;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;

public class LoIRegistry {

    public static CapabilityKey<PostureCap> POSTURE_CAP_KEY;
    public static CapabilityKey<HealthCap> HEALTH_CAP_KEY;
    public static CapabilityKey<StaminaCap> STAMINA_CAP_KEY;
    public static CapabilityKey<StatusEffectCap> STATUS_EFFECT_KEY;
    public static CapabilityKey<StanceCap> STANCE_CAP_KEY;
    public static StaticAnchorResolver staticResolver = new StaticAnchorResolver();
    public static DynamicAnchorResolver dynamicResolver = new DynamicAnchorResolver();
    public static void registerAll() {
        POSTURE_CAP_KEY = new CapabilityKey<>("posture", PostureCap.class);
        HEALTH_CAP_KEY = new CapabilityKey<>("health", HealthCap.class);
        STAMINA_CAP_KEY = new CapabilityKey<>("stamina", StaminaCap.class);
        STATUS_EFFECT_KEY = new CapabilityKey<>("status_effects", StatusEffectCap.class);
        STANCE_CAP_KEY = new CapabilityKey<>("stance", StanceCap.class);
        LoICore.context().registerData(STANCE_CAP_KEY, new StanceCap());
        LoICore.context().registerData(POSTURE_CAP_KEY, new PostureCap());
        LoICore.context().registerData(HEALTH_CAP_KEY, new HealthCap(100));
        LoICore.context().registerData(STAMINA_CAP_KEY, new StaminaCap(100));
        LoICore.context().registerData(STATUS_EFFECT_KEY, new StatusEffectCap());
        AnchorResolverRegistry.registerResolver(AnchorType.STATIC, staticResolver);
        AnchorResolverRegistry.registerResolver(AnchorType.DYNAMIC, dynamicResolver);
        dynamicResolver.registerProvider(AnchorKeys.HAND_R, new PoseGraphAnchorProvider("player", PlayerAnchorMap.INSTANCE));
        dynamicResolver.registerProvider(AnchorKeys.HAND_L, new PoseGraphAnchorProvider("player", PlayerAnchorMap.INSTANCE));

// etc

        LoILog.info("Registered core capabilities");
    }
}
