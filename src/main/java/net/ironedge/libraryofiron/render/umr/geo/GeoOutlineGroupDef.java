package net.ironedge.libraryofiron.render.umr.geo;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class GeoOutlineGroupDef {

    private final String id;
    private final String rootBone;
    private final Set<String> memberBones;
    private final boolean enabled;
    private final float widthMultiplier;

    private GeoOutlineGroupDef(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.rootBone = Objects.requireNonNull(builder.rootBone, "rootBone");
        this.memberBones = Set.copyOf(builder.memberBones);
        this.enabled = builder.enabled;
        this.widthMultiplier = builder.widthMultiplier;
    }

    public String id() {
        return id;
    }

    public String rootBone() {
        return rootBone;
    }

    public Set<String> memberBones() {
        return memberBones;
    }

    public boolean enabled() {
        return enabled;
    }

    public float widthMultiplier() {
        return widthMultiplier;
    }

    public static Builder builder(String id, String rootBone) {
        return new Builder(id, rootBone);
    }

    public static final class Builder {
        private final String id;
        private final String rootBone;
        private final Set<String> memberBones = new LinkedHashSet<>();
        private boolean enabled = true;
        private float widthMultiplier = 1.0f;

        public Builder(String id, String rootBone) {
            this.id = Objects.requireNonNull(id, "id");
            this.rootBone = Objects.requireNonNull(rootBone, "rootBone");
            this.memberBones.add(rootBone);
        }

        public Builder memberBone(String boneName) {
            this.memberBones.add(Objects.requireNonNull(boneName, "boneName"));
            return this;
        }

        public Builder memberBones(Iterable<String> boneNames) {
            for (String boneName : boneNames) {
                this.memberBones.add(Objects.requireNonNull(boneName, "boneName"));
            }
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder widthMultiplier(float widthMultiplier) {
            this.widthMultiplier = widthMultiplier;
            return this;
        }

        public GeoOutlineGroupDef build() {
            return new GeoOutlineGroupDef(this);
        }
    }
}