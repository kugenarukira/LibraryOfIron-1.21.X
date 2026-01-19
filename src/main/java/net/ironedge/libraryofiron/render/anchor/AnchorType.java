package net.ironedge.libraryofiron.render.anchor;

public enum AnchorType {
    STATIC,     // bone / vertex group
    PART,       // attached-to-attached (armor → body)
    DYNAMIC,    // physics-driven (tentacles, tails)
    PATH        // spline/path-based anchors
}
