package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchorResolver;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;

public final class PhysicsAnchorInstaller {

    private PhysicsAnchorInstaller() {}

    public static void installChain(
            DynamicAnchorResolver resolver,
            String simulationId,
            String keyPrefix,
            int pointCount
    ) {
        // POINT ANCHORS: debug_chain_p0, debug_chain_p1, ...
        for (int i = 0; i < pointCount; i++) {
            AnchorKey key = new AnchorKey(keyPrefix + "_p" + i);
            resolver.registerProvider(key, new PhysicsPointAnchorProvider(simulationId, i));
        }

        // SEGMENT ANCHORS: debug_chain_s0, debug_chain_s1, ...
        for (int i = 0; i < pointCount - 1; i++) {
            AnchorKey key = new AnchorKey(keyPrefix + "_s" + i);
            resolver.registerProvider(key, new PhysicsSegmentAnchorProvider(simulationId, i, i + 1));
        }

        // Tip is always last point
        resolver.registerProvider(
                AnchorKeys.PHYSICS_CHAIN_TIP,
                new PhysicsPointAnchorProvider(simulationId, pointCount - 1)
        );
    }
}