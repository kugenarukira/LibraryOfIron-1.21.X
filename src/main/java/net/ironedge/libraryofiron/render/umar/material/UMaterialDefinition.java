package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.shader.UShaderProfile;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class UMaterialDefinition {

    private final ResourceLocation id;
    private final UShaderProfile shaderProfile;
    private final List<UMaterialLayer> layers;
    private final Set<UShaderFeature> globalFeatures;
    private final UMaterialParamSet defaultParams;

    private UMaterialDefinition(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.shaderProfile = Objects.requireNonNull(builder.shaderProfile, "shaderProfile");

        List<UMaterialLayer> sortedLayers = new ArrayList<>(builder.layers);
        sortedLayers.sort(Comparator.comparingInt(UMaterialLayer::priority));
        this.layers = Collections.unmodifiableList(sortedLayers);

        this.globalFeatures = Collections.unmodifiableSet(EnumSet.copyOf(builder.globalFeatures));
        this.defaultParams = builder.defaultParams.copy();
    }

    public ResourceLocation id() {
        return id;
    }

    public UShaderProfile shaderProfile() {
        return shaderProfile;
    }

    public List<UMaterialLayer> layers() {
        return layers;
    }

    public Set<UShaderFeature> globalFeatures() {
        return globalFeatures;
    }

    public boolean hasGlobalFeature(UShaderFeature feature) {
        return globalFeatures.contains(feature);
    }

    public UMaterialParamSet defaultParams() {
        return defaultParams.copy();
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private UShaderProfile shaderProfile = UShaderProfile.LIT;
        private final List<UMaterialLayer> layers = new ArrayList<>();
        private final Set<UShaderFeature> globalFeatures = EnumSet.noneOf(UShaderFeature.class);
        private UMaterialParamSet defaultParams = new UMaterialParamSet();

        public Builder(ResourceLocation id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder shaderProfile(UShaderProfile shaderProfile) {
            this.shaderProfile = Objects.requireNonNull(shaderProfile, "shaderProfile");
            return this;
        }

        public Builder layer(UMaterialLayer layer) {
            this.layers.add(Objects.requireNonNull(layer, "layer"));
            return this;
        }

        public Builder globalFeature(UShaderFeature feature) {
            this.globalFeatures.add(Objects.requireNonNull(feature, "feature"));
            return this;
        }

        public Builder globalFeatures(Set<UShaderFeature> features) {
            this.globalFeatures.addAll(Objects.requireNonNull(features, "features"));
            return this;
        }

        public Builder defaultParams(UMaterialParamSet defaultParams) {
            this.defaultParams = Objects.requireNonNull(defaultParams, "defaultParams").copy();
            return this;
        }

        public UMaterialDefinition build() {
            return new UMaterialDefinition(this);
        }
    }
}