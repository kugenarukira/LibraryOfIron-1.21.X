package net.ironedge.libraryofiron.render.umr.geo;

public enum GeoRenderMask {
    DEFAULT(1 << 0),
    OUTLINE(1 << 1),
    GHOST(1 << 2),
    EMISSIVE(1 << 3),
    DEBUG(1 << 4),
    ALL(~0);

    public final int bit;
    GeoRenderMask(int bit) { this.bit = bit; }

    public static boolean matches(int boneMask, int activeMask) {
        return (boneMask & activeMask) != 0;
    }
}