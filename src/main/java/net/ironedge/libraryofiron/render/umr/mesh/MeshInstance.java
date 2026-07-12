package net.ironedge.libraryofiron.render.umr.mesh;

import net.ironedge.libraryofiron.render.umr.mesh.attachment.MeshAttachment;
import net.ironedge.libraryofiron.render.umr.morph.MorphState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class MeshInstance {

    private final String id;
    private final MeshAsset asset;
    private final MorphState morphState = new MorphState();

    private final Vector3f translation = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1f, 1f, 1f);
    private MeshAttachment attachment;
    private net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose skeletonPose;

    public MeshInstance(String id, MeshAsset asset) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (asset == null) {
            throw new IllegalArgumentException("asset must not be null");
        }

        this.id = id;
        this.asset = asset;
    }

    public net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose skeletonPose() {
        return skeletonPose;
    }

    public MeshInstance skeletonPose(net.ironedge.libraryofiron.render.umr.skeleton.SkeletonPose pose) {
        this.skeletonPose = pose;
        return this;
    }

    public String id() {
        return id;
    }

    public MeshAsset asset() {
        return asset;
    }

    public MorphState morphState() {
        return morphState;
    }

    public Vector3f translation() {
        return new Vector3f(translation);
    }

    public Quaternionf rotation() {
        return new Quaternionf(rotation);
    }

    public Vector3f scale() {
        return new Vector3f(scale);
    }

    public MeshInstance translation(Vector3f t) {
        this.translation.set(t);
        return this;
    }

    public MeshInstance rotation(Quaternionf q) {
        this.rotation.set(q);
        return this;
    }

    public MeshInstance scale(Vector3f s) {
        this.scale.set(s);
        return this;
    }

    public MeshInstance attachment(MeshAttachment attachment) {
        this.attachment = attachment;
        return this;
    }

    public MeshAttachment attachment() {
        return attachment;
    }
}