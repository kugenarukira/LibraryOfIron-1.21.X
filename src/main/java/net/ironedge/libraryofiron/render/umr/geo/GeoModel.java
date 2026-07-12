package net.ironedge.libraryofiron.render.umr.geo;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

public final class GeoModel {
    public final int texW;
    public final int texH;
    public final List<GeoBone> bones;
    public final Map<String, GeoBone> boneByName;

    public GeoModel(int texW, int texH, List<GeoBone> bones) {
        this.texW = texW;
        this.texH = texH;
        this.bones = bones;
        this.boneByName = new HashMap<>();
        for (GeoBone b : bones) boneByName.put(b.name, b);
    }

    public GeoBone bone(String name) { return boneByName.get(name); }

    public static final class GeoBone {
        public final String name;
        public final String parent; // may be null
        public final Vector3f pivotPx;      // pixels
        public final Vector3f rotationDeg;  // degrees (can be null -> treat as 0)
        public final List<GeoCube> cubes;

        public GeoBone(String name, String parent, Vector3f pivotPx, Vector3f rotationDeg, List<GeoCube> cubes) {
            this.name = name;
            this.parent = parent;
            this.pivotPx = pivotPx;
            this.rotationDeg = rotationDeg != null ? rotationDeg : new Vector3f();
            this.cubes = cubes != null ? cubes : List.of();
        }
    }

    public enum Face { NORTH, SOUTH, EAST, WEST, UP, DOWN }

    public static final class FaceUV {
        public final Vector2i uvPx;      // top-left in pixels (as in file)
        public final Vector2i uvSizePx;  // can be negative on Y (flip)

        public FaceUV(Vector2i uvPx, Vector2i uvSizePx) {
            this.uvPx = uvPx;
            this.uvSizePx = uvSizePx;
        }
    }

    public static final class GeoCube {
        public final Vector3f originPx;
        public final Vector3f sizePx;
        public final Vector3f pivotPx;       // optional; null => cube origin-based
        public final Vector3f rotationDeg;   // optional; null => 0
        public final EnumMap<Face, FaceUV> faces;

        public GeoCube(Vector3f originPx, Vector3f sizePx, Vector3f pivotPx, Vector3f rotationDeg,
                       EnumMap<Face, FaceUV> faces) {
            this.originPx = originPx;
            this.sizePx = sizePx;
            this.pivotPx = pivotPx;
            this.rotationDeg = rotationDeg != null ? rotationDeg : new Vector3f();
            this.faces = faces != null ? faces : new EnumMap<>(Face.class);
        }
    }
}