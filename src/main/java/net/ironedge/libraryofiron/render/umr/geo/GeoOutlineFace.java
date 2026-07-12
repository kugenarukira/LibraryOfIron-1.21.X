package net.ironedge.libraryofiron.render.umr.geo;

import org.joml.Vector2f;
import org.joml.Vector3f;

public final class GeoOutlineFace {

    private final Vector3f a;
    private final Vector3f b;
    private final Vector3f c;
    private final Vector3f d;
    private final Vector2f uvA;
    private final Vector2f uvB;
    private final Vector2f uvC;
    private final Vector2f uvD;
    private final Vector3f normal;

    public GeoOutlineFace(
            Vector3f a,
            Vector3f b,
            Vector3f c,
            Vector3f d,
            Vector2f uvA,
            Vector2f uvB,
            Vector2f uvC,
            Vector2f uvD,
            Vector3f normal
    ) {
        this.a = new Vector3f(a);
        this.b = new Vector3f(b);
        this.c = new Vector3f(c);
        this.d = new Vector3f(d);
        this.uvA = new Vector2f(uvA);
        this.uvB = new Vector2f(uvB);
        this.uvC = new Vector2f(uvC);
        this.uvD = new Vector2f(uvD);
        this.normal = new Vector3f(normal);
    }

    public Vector3f a() { return new Vector3f(a); }
    public Vector3f b() { return new Vector3f(b); }
    public Vector3f c() { return new Vector3f(c); }
    public Vector3f d() { return new Vector3f(d); }

    public Vector2f uvA() { return new Vector2f(uvA); }
    public Vector2f uvB() { return new Vector2f(uvB); }
    public Vector2f uvC() { return new Vector2f(uvC); }
    public Vector2f uvD() { return new Vector2f(uvD); }

    public Vector3f normal() { return new Vector3f(normal); }
}