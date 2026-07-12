package net.ironedge.libraryofiron.render.physics.forces;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsForce;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.ironedge.libraryofiron.render.physics.surface.SurfaceTopology;
import org.joml.Vector3f;

public final class WindForce implements PhysicsForce {

    private final SurfaceTopology topology;
    private final float strength;
    private final float flutter;
    private final Vector3f baseDirection;

    public WindForce(
            SurfaceTopology topology,
            float strength,
            float flutter,
            Vector3f baseDirection
    ) {
        this.topology = topology;
        this.strength = strength;
        this.flutter = flutter;
        this.baseDirection = new Vector3f(baseDirection).normalize();
    }

    public WindForce(SurfaceTopology topology, WindPreset preset) {
        this(
                topology,
                preset.strength,
                preset.flutter,
                preset.direction
        );
    }

    @Override
    public void apply(PhysicsSimulation sim, FrameContext frame, float dt) {
        var level = frame.attachment("level", net.minecraft.world.level.Level.class);
        float t = (level != null ? level.getGameTime() : 0L) + frame.partialTicks();

        for (int row = 0; row < topology.rows() - 1; row++) {
            for (int col = 0; col < topology.cols() - 1; col++) {
                int ia = topology.index(row, col);
                int ib = topology.index(row + 1, col);
                int ic = topology.index(row + 1, col + 1);
                int id = topology.index(row, col + 1);

                PhysicsPoint a = sim.points().get(ia);
                PhysicsPoint b = sim.points().get(ib);
                PhysicsPoint c = sim.points().get(ic);
                PhysicsPoint d = sim.points().get(id);

                Vector3f pa = new Vector3f(a.position);
                Vector3f pb = new Vector3f(b.position);
                Vector3f pc = new Vector3f(c.position);
                Vector3f pd = new Vector3f(d.position);

                Vector3f ab = new Vector3f(pb).sub(pa);
                Vector3f ad = new Vector3f(pd).sub(pa);

                Vector3f normal = new Vector3f(ab).cross(ad);
                float area2 = normal.length(); // 2x triangle-ish scale
                if (area2 < 1.0e-8f) continue;
                normal.normalize();

                // signed alignment: lets wind hit both sides
                float dot = normal.dot(baseDirection);

                // visible but stable oscillation
                float wave = (float) Math.sin(t * 0.08f + row * 0.6f + col * 0.4f);
                float gust = strength + flutter * wave;

                // pressure goes along panel normal, scaled by alignment and area
                float WIND_SCALE = 100.0f;
                Vector3f force = new Vector3f(normal).mul(dot * gust * area2 * WIND_SCALE);

                // distribute to quad points
                applyPointForce(a, force, 0.25f);
                applyPointForce(b, force, 0.25f);
                applyPointForce(c, force, 0.25f);
                applyPointForce(d, force, 0.25f);
            }
        }
    }

    private static void applyPointForce(PhysicsPoint p, Vector3f force, float scale) {
        if (p.pinned) return;
        p.accumulatedForce.add(new Vector3f(force).mul(scale));
    }
}