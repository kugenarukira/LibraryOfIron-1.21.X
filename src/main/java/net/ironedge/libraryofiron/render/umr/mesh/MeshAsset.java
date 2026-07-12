package net.ironedge.libraryofiron.render.umr.mesh;

import net.ironedge.libraryofiron.render.umr.morph.MorphTarget;
import net.ironedge.libraryofiron.render.umr.skeleton.Skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MeshAsset {

    private final String id;
    private final List<MeshSurface> surfaces;
    private final Map<String, MorphTarget> morphTargets;
    private final MeshBounds bounds;
    private final Skeleton skeleton;

    public MeshAsset(
            String id,
            List<MeshSurface> surfaces,
            Map<String, MorphTarget> morphTargets
    ) {
        this(
                id,
                surfaces,
                morphTargets,
                computeBoundsSafe(surfaces),
                null
        );
    }

    public MeshAsset(
            String id,
            List<MeshSurface> surfaces,
            Map<String, MorphTarget> morphTargets,
            Skeleton skeleton
    ) {
        this(
                id,
                surfaces,
                morphTargets,
                computeBoundsSafe(surfaces),
                skeleton
        );
    }

    public MeshAsset(
            String id,
            List<MeshSurface> surfaces,
            Map<String, MorphTarget> morphTargets,
            MeshBounds bounds
    ) {
        this(
                id,
                surfaces,
                morphTargets,
                bounds,
                null
        );
    }

    public MeshAsset(
            String id,
            List<MeshSurface> surfaces,
            Map<String, MorphTarget> morphTargets,
            MeshBounds bounds,
            Skeleton skeleton
    ) {
        this.id = id;
        this.surfaces = surfaces != null ? new ArrayList<>(surfaces) : new ArrayList<>();
        this.morphTargets = morphTargets != null ? new HashMap<>(morphTargets) : new HashMap<>();
        this.bounds = bounds != null ? bounds : computeBoundsSafe(this.surfaces);
        this.skeleton = skeleton;
    }

    public String id() {
        return id;
    }

    public List<MeshSurface> surfaces() {
        return new ArrayList<>(surfaces);
    }

    public List<MeshSurface> surfacesView() {
        return surfaces;
    }

    public Map<String, MorphTarget> morphTargets() {
        return new HashMap<>(morphTargets);
    }

    public MorphTarget morphTarget(String name) {
        return morphTargets.get(name);
    }

    public MeshBounds bounds() {
        return bounds;
    }

    public Skeleton skeleton() {
        return skeleton;
    }

    public boolean hasSkeleton() {
        return skeleton != null;
    }

    private static MeshBounds computeBoundsSafe(List<MeshSurface> surfaces) {
        if (surfaces == null || surfaces.isEmpty()) {
            return new MeshBounds(
                    new org.joml.Vector3f(0f, 0f, 0f),
                    new org.joml.Vector3f(0f, 0f, 0f)
            );
        }

        return computeBounds(surfaces);
    }

    private static MeshBounds computeBounds(List<MeshSurface> surfaces) {
        MeshBounds first = surfaces.get(0).bounds();

        float minX = first.min().x;
        float minY = first.min().y;
        float minZ = first.min().z;

        float maxX = first.max().x;
        float maxY = first.max().y;
        float maxZ = first.max().z;

        for (int i = 1; i < surfaces.size(); i++) {
            MeshBounds b = surfaces.get(i).bounds();

            var min = b.min();
            var max = b.max();

            if (min.x < minX) minX = min.x;
            if (min.y < minY) minY = min.y;
            if (min.z < minZ) minZ = min.z;

            if (max.x > maxX) maxX = max.x;
            if (max.y > maxY) maxY = max.y;
            if (max.z > maxZ) maxZ = max.z;
        }

        return new MeshBounds(
                new org.joml.Vector3f(minX, minY, minZ),
                new org.joml.Vector3f(maxX, maxY, maxZ)
        );
    }
}