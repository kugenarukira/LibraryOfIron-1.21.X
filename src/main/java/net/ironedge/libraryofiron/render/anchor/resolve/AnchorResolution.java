package net.ironedge.libraryofiron.render.anchor.resolve;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import net.ironedge.libraryofiron.render.anchor.impl.DynamicAnchor;
import net.ironedge.libraryofiron.render.anchor.impl.StaticAnchor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AnchorResolution {

    private AnchorResolution() {}

    public static ResolvedAnchor resolve(Anchor anchor, AnchorResolutionContext context) {
        return resolve(anchor, context, null);
    }

    /**
     * If parentResolved != null, dynamic/static offsets are applied relative to the parent translation.
     */
    public static ResolvedAnchor resolve(Anchor anchor, AnchorResolutionContext context, ResolvedAnchor parent) {
        Vector3f local;

        if (anchor instanceof StaticAnchor sa) {
            local = new Vector3f(sa.offset());
        } else if (anchor instanceof DynamicAnchor da) {
            local = new Vector3f(da.offset()); // NO bobbing here
        } else {
            local = new Vector3f(0, 0, 0);
        }

        Vector3f world = new Vector3f(local);
        if (parent != null) {
            world.add(parent.transform().translation());
        }

        AnchorTransform transform = new AnchorTransform(world, new Quaternionf(), new Vector3f(1, 1, 1));
        return new ResolvedAnchor(anchor, transform);
    }

}
