package net.ironedge.libraryofiron.render.umr.geo;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record GeoBinding(
        AnchorKey anchor,          // nullable = inherit from parent
        Vector3f localOffset,      // blocks, in bone space
        Quaternionf localRot,      // bone-space tweak
        Vector3f localScale        // bone-space scale
) {
    public static GeoBinding anchorOnly(AnchorKey a) {
        return new GeoBinding(a, new Vector3f(), new Quaternionf(), new Vector3f(1,1,1));
    }

    public static GeoBinding inherit() {
        return new GeoBinding(null, new Vector3f(), new Quaternionf(), new Vector3f(1,1,1));
    }
}