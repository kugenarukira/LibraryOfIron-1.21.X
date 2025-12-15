package net.ironedge.libraryofiron.core.registry;

import net.ironedge.libraryofiron.capability.CapabilityKey;
import net.ironedge.libraryofiron.capability.builtin.PostureCap;
import net.ironedge.libraryofiron.core.LoICore;
import net.ironedge.libraryofiron.core.LoILog;

public class LoIRegistry {

    public static CapabilityKey<PostureCap> POSTURE_CAP_KEY;

    public static void registerAll() {
        POSTURE_CAP_KEY = new CapabilityKey<>("posture", PostureCap.class);
        LoICore.context().registerData(POSTURE_CAP_KEY, new PostureCap());
        LoILog.info("Registered PostureCap");
    }
}
