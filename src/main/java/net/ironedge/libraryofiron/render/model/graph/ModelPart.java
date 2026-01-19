package net.ironedge.libraryofiron.render.model.graph;

import net.ironedge.libraryofiron.render.anchor.Anchor;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a part of a model.
 * Each part can attach to an Anchor, have children, and store local offsets.
 */
public class ModelPart {

    private final String id;
    private final Anchor attachAnchor;
    private final List<ModelPart> children = new ArrayList<>();
    private Vector3f offset = new Vector3f(0, 0, 0);

    public ModelPart(String id, Anchor attachAnchor) {
        this.id = id;
        this.attachAnchor = attachAnchor;
    }

    public String id() { return id; }

    /** The anchor this part attaches to (static/dynamic) */
    public Anchor attachAnchor() { return attachAnchor; }

    public Vector3f offset() { return offset; }
    public void offset(Vector3f offset) { this.offset = offset; }

    /** Add a child part (for hierarchical models) */
    public void addChild(ModelPart part) { children.add(part); }
    public List<ModelPart> children() { return children; }
}
