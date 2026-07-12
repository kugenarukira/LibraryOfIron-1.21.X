package net.ironedge.libraryofiron.render.physics.collision;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsPoint;
import net.ironedge.libraryofiron.render.physics.PhysicsSimulation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public final class BlockCollisionSolver {

    private BlockCollisionSolver() {}

    public static void solve(PhysicsSimulation sim, FrameContext frame) {
        Level level = frame.attachment("level", Level.class);
        if (level == null) return;

        for (PhysicsPoint p : sim.points()) {
            if (p.pinned) continue;
            solvePointVsBlocks(level, p);
        }
    }

    private static void solvePointVsBlocks(Level level, PhysicsPoint p) {
        float r = p.radius;

        int minX = (int) Math.floor(p.position.x - r);
        int maxX = (int) Math.floor(p.position.x + r);
        int minY = (int) Math.floor(p.position.y - r);
        int maxY = (int) Math.floor(p.position.y + r);
        int minZ = (int) Math.floor(p.position.z - r);
        int maxZ = (int) Math.floor(p.position.z + r);

        for (int bx = minX; bx <= maxX; bx++) {
            for (int by = minY; by <= maxY; by++) {
                for (int bz = minZ; bz <= maxZ; bz++) {

                    BlockPos pos = new BlockPos(bx, by, bz);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()) continue;

                    var shape = state.getCollisionShape(level, pos);
                    if (shape.isEmpty()) continue;

                    final int blockX = bx;
                    final int blockY = by;
                    final int blockZ = bz;
                    final float radius = r;

                    shape.forAllBoxes((minBX, minBY, minBZ, maxBX, maxBY, maxBZ) -> {
                        float minXw = (float) (blockX + minBX);
                        float minYw = (float) (blockY + minBY);
                        float minZw = (float) (blockZ + minBZ);

                        float maxXw = (float) (blockX + maxBX);
                        float maxYw = (float) (blockY + maxBY);
                        float maxZw = (float) (blockZ + maxBZ);

                        pushOutOfAABB(p, minXw, minYw, minZw, maxXw, maxYw, maxZw, radius);
                    });
                }
            }
        }
    }

    private static void pushOutOfAABB(
            PhysicsPoint p,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ,
            float radius
    ) {

        float cx = clamp(p.position.x, minX, maxX);
        float cy = clamp(p.position.y, minY, maxY);
        float cz = clamp(p.position.z, minZ, maxZ);

        float dx = p.position.x - cx;
        float dy = p.position.y - cy;
        float dz = p.position.z - cz;

        float distSq = dx * dx + dy * dy + dz * dz;
        float radiusSq = radius * radius;

        if (distSq >= radiusSq) return;

        float dist = (float) Math.sqrt(distSq);

        if (dist > 1e-6f) {

            float push = radius - dist;

            float nx = dx / dist;
            float ny = dy / dist;
            float nz = dz / dist;

            p.position.add(
                    nx * push,
                    ny * push,
                    nz * push
            );

            return;
        }

        // inside shape fallback

        float left   = p.position.x - minX;
        float right  = maxX - p.position.x;
        float down   = p.position.y - minY;
        float up     = maxY - p.position.y;
        float back   = p.position.z - minZ;
        float front  = maxZ - p.position.z;

        float best = left;
        int axis = 0;
        float sign = -1f;

        if (right < best) { best = right; axis = 0; sign =  1f; }
        if (down  < best) { best = down;  axis = 1; sign = -1f; }
        if (up    < best) { best = up;    axis = 1; sign =  1f; }
        if (back  < best) { best = back;  axis = 2; sign = -1f; }
        if (front < best) { best = front; axis = 2; sign =  1f; }

        float push = radius + best;

        if (axis == 0) p.position.x += sign * push;
        if (axis == 1) p.position.y += sign * push;
        if (axis == 2) p.position.z += sign * push;
    }

    private static void pushOutOfUnitBlock(PhysicsPoint p, int bx, int by, int bz, float radius) {
        float minX = bx;
        float minY = by;
        float minZ = bz;
        float maxX = bx + 1.0f;
        float maxY = by + 1.0f;
        float maxZ = bz + 1.0f;

        float cx = clamp(p.position.x, minX, maxX);
        float cy = clamp(p.position.y, minY, maxY);
        float cz = clamp(p.position.z, minZ, maxZ);

        float dx = p.position.x - cx;
        float dy = p.position.y - cy;
        float dz = p.position.z - cz;

        float distSq = dx * dx + dy * dy + dz * dz;
        float radiusSq = radius * radius;

        if (distSq >= radiusSq) return;

        Vector3f pointBefore = new Vector3f(p.position);
        Vector3f contactPos = new Vector3f(cx, cy, cz);

        // Normal sphere-vs-AABB push
        if (distSq > 1.0e-12f) {
            float dist = (float) Math.sqrt(distSq);
            float push = radius - dist;

            Vector3f pushDir = new Vector3f(dx, dy, dz).div(dist);

            p.position.add(
                    pushDir.x * push,
                    pushDir.y * push,
                    pushDir.z * push
            );

            net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionDebugState.add(
                    new net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionContact(
                            pointBefore,
                            contactPos,
                            new Vector3f(pushDir),
                            push
                    )
            );
            return;
        }

        // Degenerate case: center inside block / exactly on closest point
        float left   = p.position.x - minX;
        float right  = maxX - p.position.x;
        float down   = p.position.y - minY;
        float up     = maxY - p.position.y;
        float back   = p.position.z - minZ;
        float front  = maxZ - p.position.z;

        float best = left;
        int axis = 0;
        float sign = -1f;

        if (right < best) { best = right; axis = 0; sign =  1f; }
        if (down  < best) { best = down;  axis = 1; sign = -1f; }
        if (up    < best) { best = up;    axis = 1; sign =  1f; }
        if (back  < best) { best = back;  axis = 2; sign = -1f; }
        if (front < best) { best = front; axis = 2; sign =  1f; }

        float push = radius + best;

        Vector3f pushDir = new Vector3f();
        if (axis == 0) {
            p.position.x += sign * push;
            pushDir.set(sign, 0f, 0f);
        }
        if (axis == 1) {
            p.position.y += sign * push;
            pushDir.set(0f, sign, 0f);
        }
        if (axis == 2) {
            p.position.z += sign * push;
            pushDir.set(0f, 0f, sign);
        }

        net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionDebugState.add(
                new net.ironedge.libraryofiron.render.physics.collision.PhysicsCollisionContact(
                        pointBefore,
                        contactPos,
                        pushDir,
                        push
                )
        );
    }

    private static float clamp(float v, float lo, float hi) {
        return (v < lo) ? lo : Math.min(v, hi);
    }
}