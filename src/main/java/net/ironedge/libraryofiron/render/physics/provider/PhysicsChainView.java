package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class PhysicsChainView {
    private PhysicsChainView() {}

    public static List<Vector3f> samplePoints(String simulationId) {
        PhysicsSimulation sim = PhysicsSystem.get().getById(simulationId);
        List<Vector3f> out = new ArrayList<>();
        if (sim == null) return out;

        for (var p : sim.points()) {
            out.add(new Vector3f(p.position));
        }
        return out;
    }
}