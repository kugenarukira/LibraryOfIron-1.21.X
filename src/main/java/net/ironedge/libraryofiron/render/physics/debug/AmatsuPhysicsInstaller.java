package net.ironedge.libraryofiron.render.physics.debug;


import net.ironedge.libraryofiron.core.registry.LoIRegistry;
import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsPointAnchorProvider;
import net.ironedge.libraryofiron.render.physics.provider.PhysicsStartSegmentAnchorProvider;

public final class AmatsuPhysicsInstaller {
    private AmatsuPhysicsInstaller() {}

    public static void install(String simulationId, int pointCount) {
        // points
        for (int i = 0; i < pointCount; i++) {
            LoIRegistry.dynamicResolver.registerProvider(
                    new AnchorKey("amatsuquiz_p" + i),
                    new PhysicsPointAnchorProvider(simulationId, i)
            );
        }

        // start-pivot segments
        for (int i = 0; i < pointCount - 1; i++) {
            LoIRegistry.dynamicResolver.registerProvider(
                    new AnchorKey("amatsuquiz_s" + i),
                    new PhysicsStartSegmentAnchorProvider(simulationId, i, i + 1)
            );
        }

        // tip
        LoIRegistry.dynamicResolver.registerProvider(
                AnchorKeys.PHYSICS_CHAIN_TIP,
                new PhysicsPointAnchorProvider(simulationId, pointCount - 1)
        );
    }
}