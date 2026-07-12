package net.ironedge.libraryofiron.render.physics.surface;

import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.PhysicsSystem;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class PhysicsSurfaceView {

    private PhysicsSurfaceView() {}

    public static PhysicsSimulation simulation(String simulationId) {
        return PhysicsSystem.get().getById(simulationId);
    }

    public static Vector3f point(String simulationId, SurfaceTopology topo, int row, int col) {
        PhysicsSimulation sim = simulation(simulationId);
        if (sim == null) return null;

        int idx = topo.index(row, col);
        if (idx < 0 || idx >= sim.points().size()) return null;

        PhysicsPoint p = sim.points().get(idx);
        return new Vector3f(p.position);
    }

    public static List<Vector3f> row(String simulationId, SurfaceTopology topo, int row) {
        List<Vector3f> out = new ArrayList<>();
        for (int col = 0; col < topo.cols(); col++) {
            Vector3f p = point(simulationId, topo, row, col);
            if (p != null) out.add(p);
        }
        return out;
    }
}