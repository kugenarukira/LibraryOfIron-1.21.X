package net.ironedge.libraryofiron.render.physics.provider;

import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.physics.PhysicsTarget;
import org.joml.Vector3f;

public final class OffsetPhysicsTarget implements PhysicsTarget {
    private final PhysicsTarget base;
    private final Vector3f offset;

    public OffsetPhysicsTarget(PhysicsTarget base, Vector3f offset) {
        this.base = base;
        this.offset = new Vector3f(offset);
    }

    @Override
    public Vector3f samplePosition(FrameContext frame) {
        Vector3f p = base.samplePosition(frame);
        return (p == null) ? null : p.add(new Vector3f(offset));
    }
}