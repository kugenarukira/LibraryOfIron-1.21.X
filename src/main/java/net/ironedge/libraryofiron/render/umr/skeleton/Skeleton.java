package net.ironedge.libraryofiron.render.umr.skeleton;

import java.util.List;

public final class Skeleton {

    private final List<Bone> bones;

    public Skeleton(List<Bone> bones) {
        this.bones = List.copyOf(bones);
    }

    public int boneCount() {
        return bones.size();
    }

    public Bone bone(int index) {
        return bones.get(index);
    }

    public List<Bone> bones() {
        return bones;
    }
}