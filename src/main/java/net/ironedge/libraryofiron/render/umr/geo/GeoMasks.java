package net.ironedge.libraryofiron.render.umr.geo;

public final class GeoMasks {
    private GeoMasks() {}
    public static boolean matches(int boneMask, int activeMask) {
        return (boneMask & activeMask) != 0;
    }
}