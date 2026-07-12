
package net.ironedge.libraryofiron.render.umr.geo;

import org.joml.*;

import java.lang.Math;

import static net.ironedge.libraryofiron.render.umr.geo.GeoModel.*;

public final class GeoBaker {

    private GeoBaker() {}

    // pixel -> block
    public static final float PX = 1f / 16f;

    private static Quaternionf bedrockEulerToMcQuat(Vector3f bedrockDeg) {
        // mirror on Y means X and Z angles invert
        Vector3f mcDeg = new Vector3f(-bedrockDeg.x, bedrockDeg.y, -bedrockDeg.z);
        return eulerDegXYZ(mcDeg);
    }

    private static Vector3f bedrockToMcPoint(Vector3f bedrockBlockPos) {
        // bedrockBlockPos is already in blocks (px * PX)
        // y' = 24px - y  (in block units)
        return new Vector3f(
                bedrockBlockPos.x,
                24f * PX - bedrockBlockPos.y,
                bedrockBlockPos.z
        );
    }
    // NEW: bake one cube into a specific bone mesh
    private static void bakeCubeIntoBone(GeoModel model, GeoBone bone, GeoCube c, Vector3f pivotMc, GeoMesh.BoneMesh out) {
        // NOTE: right now we are NOT doing bone parent transforms here.
        // This is fine for your current debug file because "armorX" bones are under biped bones but have no extra offsets.
        // We'll upgrade this in the "hard part" step to use full parent matrices.

        // Build a transform matrix for cube rotation about its pivot in BEDROCK space
        Matrix4f cubeM = new Matrix4f();

        Vector3f cubePivot = (c.pivotPx != null ? new Vector3f(c.pivotPx) : new Vector3f(c.originPx)).mul(PX);
        Quaternionf cq = eulerDegXYZ(new Vector3f(c.rotationDeg));

        cubeM.translate(cubePivot).rotate(cq).translate(new Vector3f(cubePivot).mul(-1f));

        // Cube corners in bedrock block space
        Vector3f o = new Vector3f(c.originPx).mul(PX);
        Vector3f s = new Vector3f(c.sizePx).mul(PX);

        float x0=o.x, y0=o.y, z0=o.z;
        float x1=o.x+s.x, y1=o.y+s.y, z1=o.z+s.z;

        // Determine if we need to flip winding due to the Bedrock->MC handedness conversion.
        // Your conversion y' = 24px - y is effectively a mirror (negative determinant), so flip winding.
        boolean flipWinding = true;

        // Emit faces. Points are still in bedrock block coordinates; vert() will transform them.
        emitFace(model, c, Face.NORTH, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x1,y0,z0), new Vector3f(x0,y0,z0), new Vector3f(x0,y1,z0), new Vector3f(x1,y1,z0));
        emitFace(model, c, Face.SOUTH, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x0,y0,z1), new Vector3f(x1,y0,z1), new Vector3f(x1,y1,z1), new Vector3f(x0,y1,z1));
        emitFace(model, c, Face.EAST, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x1,y0,z1), new Vector3f(x1,y0,z0), new Vector3f(x1,y1,z0), new Vector3f(x1,y1,z1));
        emitFace(model, c, Face.WEST, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x0,y0,z0), new Vector3f(x0,y0,z1), new Vector3f(x0,y1,z1), new Vector3f(x0,y1,z0));
        emitFace(model, c, Face.UP, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x1,y1,z0), new Vector3f(x0,y1,z0), new Vector3f(x0,y1,z1), new Vector3f(x1,y1,z1));
        emitFace(model, c, Face.DOWN, cubeM, pivotMc, flipWinding, out,
                new Vector3f(x1,y0,z1), new Vector3f(x0,y0,z1), new Vector3f(x0,y0,z0), new Vector3f(x1,y0,z0));
    }

    public static GeoMesh bake(GeoModel model) {
        GeoMesh mesh = new GeoMesh();

        for (GeoBone bone : model.bones) {
            GeoMesh.BoneMesh bm = new GeoMesh.BoneMesh();
            bm.parent = bone.parent;

            Vector3f pivotMc = bedrockToMcPoint(new Vector3f(bone.pivotPx).mul(PX));
            bm.pivotMc = new Vector3f(pivotMc);

            // ✅ bake rest rotation
            bm.restRotMc = bedrockEulerToMcQuat(new Vector3f(bone.rotationDeg));

            mesh.bones.put(bone.name, bm);

            for (GeoCube cube : bone.cubes) {
                bakeCubeIntoBone(model, bone, cube, pivotMc, bm);
            }
        }

        return mesh;
    }

    private static Matrix4f computeBoneWorld(GeoModel model,
                                             GeoModel.GeoBone bone,
                                             java.util.Map<String, Matrix4f> cache) {
        Matrix4f cached = cache.get(bone.name);
        if (cached != null) return new Matrix4f(cached);

        Matrix4f local = boneTransform(bone);

        if (bone.parent != null) {
            GeoModel.GeoBone parent = model.bone(bone.parent);
            if (parent != null) {
                Matrix4f parentWorld = computeBoneWorld(model, parent, cache);
                Matrix4f world = new Matrix4f(parentWorld).mul(local);
                cache.put(bone.name, world);
                return new Matrix4f(world);
            }
        }

        cache.put(bone.name, local);
        return new Matrix4f(local);
    }

    private static Matrix4f boneTransform(GeoBone b) {
        // Bedrock rotations are around pivot in *pixels*
        Vector3f pivot = new Vector3f(b.pivotPx).mul(PX);
        Vector3f rot = new Vector3f(b.rotationDeg);

        Quaternionf q = eulerDegXYZ(rot); // bedrock uses degrees; axis order matters—XYZ is common in BB
        return new Matrix4f()
                .translate(pivot)
                .rotate(q)
                .translate(new Vector3f(pivot).mul(-1f));
    }


    private static Vector3f faceNormal(GeoMesh.Vertex a, GeoMesh.Vertex b, GeoMesh.Vertex c) {
        Vector3f ab = new Vector3f(b.pos).sub(a.pos);
        Vector3f ac = new Vector3f(c.pos).sub(a.pos);
        Vector3f n = ab.cross(ac); // right-hand rule
        if (n.lengthSquared() < 1.0e-12f) return new Vector3f(0, 1, 0); // degenerate safety
        return n.normalize();
    }
    private static GeoMesh.Vertex vertBoneLocal(Matrix4f cubeM, Vector3f pivotMc, Vector3f pBedrock, float u, float v) {
        // 1) rotate cube point in bedrock block space
        Vector3f rotated = transform(cubeM, pBedrock);

        // 2) convert bedrock->mc coordinate space
        Vector3f mc = bedrockToMcPoint(rotated);

        // 3) make it bone-local (pivot at 0)
        mc.sub(pivotMc);

        return new GeoMesh.Vertex(mc, u, v);
    }

    private static void emitFace(GeoModel model, GeoCube cube, Face face,
                                 Matrix4f cubeM, Vector3f pivotMc,
                                 boolean flipWinding, GeoMesh.BoneMesh out,
                                 Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {

        FaceUV fuv = cube.faces.get(face);
        if (fuv == null) return;

        float uPx = fuv.uvPx.x;
        float vPx = fuv.uvPx.y;
        float wPx = fuv.uvSizePx.x;
        float hPx = fuv.uvSizePx.y;

        float u0 = uPx;
        float v0 = vPx;
        float u1 = uPx + wPx;
        float v1 = vPx + hPx;

        float nu0 = u0 / model.texW;
        float nv0 = v0 / model.texH;
        float nu1 = u1 / model.texW;
        float nv1 = v1 / model.texH;

        GeoMesh.Vertex a, b, cV, d;

        switch (face) {
            case NORTH -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu1, nv0);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu0, nv0);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu0, nv1);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu1, nv1);
            }
            case SOUTH -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu0, nv0);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu1, nv0);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu1, nv1);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu0, nv1);
            }
            case EAST -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu0, nv0);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu1, nv0);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu1, nv1);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu0, nv1);
            }
            case WEST -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu1, nv0);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu0, nv0);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu0, nv1);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu1, nv1);
            }
            case UP -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu1, nv1);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu0, nv1);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu0, nv0);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu1, nv0);
            }
            case DOWN -> {
                a  = vertBoneLocal(cubeM, pivotMc, p0, nu1, nv0);
                b  = vertBoneLocal(cubeM, pivotMc, p1, nu0, nv0);
                cV = vertBoneLocal(cubeM, pivotMc, p2, nu0, nv1);
                d  = vertBoneLocal(cubeM, pivotMc, p3, nu1, nv1);
            }
            default -> { return; }
        }

        if (flipWinding) {
            GeoMesh.Vertex tmp = b;
            b = d;
            d = tmp;
        }

        Vector3f n = faceNormal(a, b, cV);
        out.quads.add(new GeoMesh.Quad(a, b, cV, d, n));
    }

    private static Vector3f transform(Matrix4f M, Vector3f p) {
        Vector4f v = new Vector4f(p.x, p.y, p.z, 1f).mul(M);
        return new Vector3f(v.x, v.y, v.z);
    }

    private static Quaternionf eulerDegXYZ(Vector3f deg) {
        // degrees -> radians
        float rx = (float) Math.toRadians(deg.x);
        float ry = (float) Math.toRadians(deg.y);
        float rz = (float) Math.toRadians(deg.z);
        return new Quaternionf().rotationXYZ(rx, ry, rz);
    }
}