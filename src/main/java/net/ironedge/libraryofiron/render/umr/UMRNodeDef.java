package net.ironedge.libraryofiron.render.umr;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public record UMRNodeDef(
        String id,
        String parentId,              // null for root
        Vector3f localTranslation,    // local to parent
        Quaternionf localRotation,    // local to parent
        Vector3f localScale           // local to parent
) {
    public static UMRNodeDef root(String id) {
        return new UMRNodeDef(id, null, new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1));
    }
}
