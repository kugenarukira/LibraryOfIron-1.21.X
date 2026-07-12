package net.ironedge.libraryofiron.render.umr.geo;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class GeoMesh {
    public final java.util.Map<String, BoneMesh> bones = new java.util.HashMap<>();

    public static final class BoneMesh {
        public String name;
        public String parent;          // null if root
        public Vector3f pivotMc;              // MC-space pivot of this bone (absolute in model space)
        public Quaternionf restRotMc = new Quaternionf();  // ✅ NEW: geo rest rotation in MC coords
        public Vector3f pivotLocalMc; // (pivotMc - parentPivotMc) cached at bake time
        public org.joml.Quaternionf geoLocalRot = new org.joml.Quaternionf(); // optional tweak/authoring correction
        public final java.util.List<Quad> quads = new java.util.ArrayList<>(); // from geo file (default identity)// in blocks, local space
        public boolean visible = true;          // default on
        public int mask = GeoRenderMask.DEFAULT.bit;  // default pass
    }

    public static final class Vertex {
        public final org.joml.Vector3f pos; // bone-local (pivot = 0)
        public final float u, v;
        public Vertex(org.joml.Vector3f pos, float u, float v) {
            this.pos = pos; this.u = u; this.v = v;
        }
    }

    public static final class Quad {
        public final Vertex a,b,c,d;
        public final org.joml.Vector3f normal; // bone-local normal
        public Quad(Vertex a, Vertex b, Vertex c, Vertex d, org.joml.Vector3f normal) {
            this.a=a; this.b=b; this.c=c; this.d=d; this.normal=normal;
        }
    }
}