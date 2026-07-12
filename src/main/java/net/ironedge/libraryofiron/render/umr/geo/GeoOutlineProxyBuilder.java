package net.ironedge.libraryofiron.render.umr.geo;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public final class GeoOutlineProxyBuilder {

    private static final float EPS = 0.0001f;

    private GeoOutlineProxyBuilder() {}

    public static GeoOutlineProxyMesh buildProxy(
            GeoMesh mesh,
            GeoOutlineGroupDef group
    ) {
        List<GeoOutlineFace> raw = collectFaces(mesh, group);
        List<GeoOutlineFace> filtered = removeOpposing(raw);
        List<GeoOutlineFace> merged = mergeCoplanar(filtered);
        return new GeoOutlineProxyMesh(merged);
    }

    private static List<GeoOutlineFace> collectFaces(
            GeoMesh mesh,
            GeoOutlineGroupDef group
    ) {
        List<GeoOutlineFace> out = new ArrayList<>();

        for (String bone : group.memberBones()) {
            GeoMesh.BoneMesh bm = mesh.bones.get(bone);
            if (bm == null) continue;

            for (GeoMesh.Quad q : bm.quads) {
                out.add(new GeoOutlineFace(
                        q.a.pos, q.b.pos, q.c.pos, q.d.pos,
                        new Vector2f(q.a.u, q.a.v),
                        new Vector2f(q.b.u, q.b.v),
                        new Vector2f(q.c.u, q.c.v),
                        new Vector2f(q.d.u, q.d.v),
                        q.normal
                ));
            }
        }

        return out;
    }

    private static List<GeoOutlineFace> removeOpposing(List<GeoOutlineFace> input) {
        List<GeoOutlineFace> out = new ArrayList<>();

        outer:
        for (GeoOutlineFace a : input) {
            for (GeoOutlineFace b : out) {
                if (isOpposingDuplicate(a, b)) {
                    continue outer;
                }
            }
            out.add(a);
        }

        return out;
    }

    private static boolean isOpposingDuplicate(
            GeoOutlineFace a,
            GeoOutlineFace b
    ) {
        return sameVerts(a, b) &&
                a.normal().dot(b.normal()) < -0.99f;
    }

    private static boolean sameVerts(
            GeoOutlineFace a,
            GeoOutlineFace b
    ) {
        return samePos(a.a(), b.a()) &&
                samePos(a.b(), b.b()) &&
                samePos(a.c(), b.c()) &&
                samePos(a.d(), b.d());
    }

    private static boolean samePos(Vector3f a, Vector3f b) {
        return Math.abs(a.x - b.x) < EPS &&
                Math.abs(a.y - b.y) < EPS &&
                Math.abs(a.z - b.z) < EPS;
    }

    private static List<GeoOutlineFace> mergeCoplanar(List<GeoOutlineFace> faces) {
        Map<String, List<GeoOutlineFace>> buckets = new LinkedHashMap<>();

        for (GeoOutlineFace face : faces) {
            String key = bucketKey(face);
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(face);
        }

        List<GeoOutlineFace> out = new ArrayList<>();

        for (List<GeoOutlineFace> bucket : buckets.values()) {
            out.addAll(mergeBucket(bucket));
        }

        return out;
    }

    private static String bucketKey(GeoOutlineFace f) {
        Vector3f n = quantizeNormal(f.normal());
        float d = planeDepth(f.a(), n);

        return n.x + "|" + n.y + "|" + n.z + "|" + round(d);
    }

    private static Vector3f quantizeNormal(Vector3f n) {
        return new Vector3f(
                Math.round(n.x),
                Math.round(n.y),
                Math.round(n.z)
        );
    }

    private static float planeDepth(Vector3f p, Vector3f n) {
        return p.x * n.x + p.y * n.y + p.z * n.z;
    }

    private static float round(float f) {
        return Math.round(f * 1000f) / 1000f;
    }

    private static List<GeoOutlineFace> mergeBucket(List<GeoOutlineFace> bucket) {
        List<GeoOutlineFace> working = new ArrayList<>(bucket);

        boolean changed = true;

        while (changed) {
            changed = false;

            for (int i = 0; i < working.size(); i++) {
                for (int j = i + 1; j < working.size(); j++) {
                    GeoOutlineFace merged = tryMerge(
                            working.get(i),
                            working.get(j)
                    );

                    if (merged != null) {
                        working.remove(j);
                        working.remove(i);
                        working.add(merged);
                        changed = true;
                        break;
                    }
                }

                if (changed) break;
            }
        }

        return working;
    }

    private static GeoOutlineFace tryMerge(
            GeoOutlineFace a,
            GeoOutlineFace b
    ) {
        Vector3f n = a.normal();

        float minX = min(a, b, Axis.X);
        float maxX = max(a, b, Axis.X);
        float minY = min(a, b, Axis.Y);
        float maxY = max(a, b, Axis.Y);
        float minZ = min(a, b, Axis.Z);
        float maxZ = max(a, b, Axis.Z);

        int lockedAxis =
                Math.abs(n.x) > 0.9f ? 0 :
                        Math.abs(n.y) > 0.9f ? 1 : 2;

        if (lockedAxis == 2) {
            return rectXY(minX,maxX,minY,maxY,a.a().z,n);
        }

        if (lockedAxis == 1) {
            return rectXZ(minX,maxX,minZ,maxZ,a.a().y,n);
        }

        return rectYZ(minY,maxY,minZ,maxZ,a.a().x,n);
    }

    private enum Axis { X,Y,Z }

    private static float min(
            GeoOutlineFace a,
            GeoOutlineFace b,
            Axis axis
    ) {
        return Math.min(faceMin(a, axis), faceMin(b, axis));
    }

    private static float max(
            GeoOutlineFace a,
            GeoOutlineFace b,
            Axis axis
    ) {
        return Math.max(faceMax(a, axis), faceMax(b, axis));
    }

    private static float faceMin(
            GeoOutlineFace f,
            Axis axis
    ) {
        return Math.min(Math.min(val(f.a(),axis), val(f.b(),axis)),
                Math.min(val(f.c(),axis), val(f.d(),axis)));
    }

    private static float faceMax(
            GeoOutlineFace f,
            Axis axis
    ) {
        return Math.max(Math.max(val(f.a(),axis), val(f.b(),axis)),
                Math.max(val(f.c(),axis), val(f.d(),axis)));
    }

    private static float val(Vector3f v, Axis axis) {
        return switch (axis) {
            case X -> v.x;
            case Y -> v.y;
            case Z -> v.z;
        };
    }

    private static GeoOutlineFace rectXY(
            float minX,float maxX,
            float minY,float maxY,
            float z,
            Vector3f n
    ) {
        return new GeoOutlineFace(
                new Vector3f(minX,minY,z),
                new Vector3f(maxX,minY,z),
                new Vector3f(maxX,maxY,z),
                new Vector3f(minX,maxY,z),
                new Vector2f(),new Vector2f(),
                new Vector2f(),new Vector2f(),
                n
        );
    }

    private static GeoOutlineFace rectXZ(
            float minX,float maxX,
            float minZ,float maxZ,
            float y,
            Vector3f n
    ) {
        return new GeoOutlineFace(
                new Vector3f(minX,y,minZ),
                new Vector3f(maxX,y,minZ),
                new Vector3f(maxX,y,maxZ),
                new Vector3f(minX,y,maxZ),
                new Vector2f(),new Vector2f(),
                new Vector2f(),new Vector2f(),
                n
        );
    }

    private static GeoOutlineFace rectYZ(
            float minY,float maxY,
            float minZ,float maxZ,
            float x,
            Vector3f n
    ) {
        return new GeoOutlineFace(
                new Vector3f(x,minY,minZ),
                new Vector3f(x,maxY,minZ),
                new Vector3f(x,maxY,maxZ),
                new Vector3f(x,minY,maxZ),
                new Vector2f(),new Vector2f(),
                new Vector2f(),new Vector2f(),
                n
        );
    }
}