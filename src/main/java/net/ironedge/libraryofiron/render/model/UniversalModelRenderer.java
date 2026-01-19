package net.ironedge.libraryofiron.render.model;

import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolution;
import net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext;
import net.ironedge.libraryofiron.render.anchor.resolve.ResolvedAnchor;
import net.ironedge.libraryofiron.render.model.graph.ModelPart;

import java.util.ArrayList;
import java.util.List;

public final class UniversalModelRenderer {

    private final List<ModelPart> roots;

    public UniversalModelRenderer(List<ModelPart> roots) {
        this.roots = roots;
    }

    /** Resolve all parts and return them (in traversal order). */
    public List<ResolvedAnchor> resolveAll(AnchorResolutionContext context) {
        List<ResolvedAnchor> out = new ArrayList<>();
        for (ModelPart root : roots) {
            resolvePart(root, context, null, out);
        }
        return out;
    }

    private void resolvePart(
            ModelPart part,
            AnchorResolutionContext context,
            ResolvedAnchor parentResolved,
            List<ResolvedAnchor> out
    ) {
        ResolvedAnchor resolved = AnchorResolution.resolve(part.attachAnchor(), context, parentResolved);
        out.add(resolved);

        for (ModelPart child : part.children()) {
            resolvePart(child, context, resolved, out);
        }
    }
}
