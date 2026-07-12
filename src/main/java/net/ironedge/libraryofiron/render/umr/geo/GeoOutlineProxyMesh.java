package net.ironedge.libraryofiron.render.umr.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GeoOutlineProxyMesh {

    private final List<GeoOutlineFace> faces;

    public GeoOutlineProxyMesh(List<GeoOutlineFace> faces) {
        this.faces = new ArrayList<>(faces);
    }

    public List<GeoOutlineFace> faces() {
        return Collections.unmodifiableList(faces);
    }

    public boolean isEmpty() {
        return faces.isEmpty();
    }

    public int size() {
        return faces.size();
    }
}