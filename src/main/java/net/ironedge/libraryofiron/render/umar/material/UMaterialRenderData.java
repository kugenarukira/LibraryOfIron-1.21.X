package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerBlendMode;
import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.shader.UShaderPass;
import net.ironedge.libraryofiron.render.umar.shader.UShaderProfile;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class UMaterialRenderData {

    private final String layerName;
    private final int priority;

    private final ULayerRenderMode renderMode;
    private final ULayerBlendMode blendMode;

    private final UShaderProfile shaderProfile;
    private final UShaderPass shaderPass;

    private final Map<UMaterialTextureSlot, UTextureBinding> textures;
    private final Set<UShaderFeature> features;

    private final UMaterialParamSet resolvedParams;

    private final int color;
    private final float alpha;
    private final float emissiveStrength;
    private final float uvScrollU;
    private final float uvScrollV;

    private UMaterialRenderData(Builder builder) {
        this.layerName = builder.layerName;
        this.priority = builder.priority;
        this.renderMode = builder.renderMode;
        this.blendMode = builder.blendMode;
        this.shaderProfile = builder.shaderProfile;
        this.shaderPass = builder.shaderPass;
        this.textures = Collections.unmodifiableMap(new EnumMap<>(builder.textures));
        this.features = Collections.unmodifiableSet(builder.features.isEmpty()
                ? EnumSet.noneOf(UShaderFeature.class)
                : EnumSet.copyOf(builder.features));
        this.resolvedParams = builder.resolvedParams.copy();
        this.color = builder.color;
        this.alpha = builder.alpha;
        this.emissiveStrength = builder.emissiveStrength;
        this.uvScrollU = builder.uvScrollU;
        this.uvScrollV = builder.uvScrollV;
    }

    public String layerName() {
        return layerName;
    }

    public int priority() {
        return priority;
    }

    public ULayerRenderMode renderMode() {
        return renderMode;
    }

    public ULayerBlendMode blendMode() {
        return blendMode;
    }

    public UShaderProfile shaderProfile() {
        return shaderProfile;
    }

    public UShaderPass shaderPass() {
        return shaderPass;
    }

    public Map<UMaterialTextureSlot, UTextureBinding> textures() {
        return textures;
    }

    public UTextureBinding texture(UMaterialTextureSlot slot) {
        return textures.get(slot);
    }

    public Set<UShaderFeature> features() {
        return features;
    }

    public boolean hasFeature(UShaderFeature feature) {
        return features.contains(feature);
    }

    public UMaterialParamSet resolvedParams() {
        return resolvedParams.copy();
    }

    public int color() {
        return color;
    }

    public float alpha() {
        return alpha;
    }

    public float emissiveStrength() {
        return emissiveStrength;
    }

    public float uvScrollU() {
        return uvScrollU;
    }

    public float uvScrollV() {
        return uvScrollV;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String layerName = "unnamed";
        private int priority = 0;

        private ULayerRenderMode renderMode = ULayerRenderMode.OPAQUE;
        private ULayerBlendMode blendMode = ULayerBlendMode.NORMAL;

        private UShaderProfile shaderProfile = UShaderProfile.LIT;
        private UShaderPass shaderPass = UShaderPass.BASE;

        private final Map<UMaterialTextureSlot, UTextureBinding> textures = new EnumMap<>(UMaterialTextureSlot.class);
        private final Set<UShaderFeature> features = EnumSet.noneOf(UShaderFeature.class);

        private UMaterialParamSet resolvedParams = new UMaterialParamSet();

        private int color = 0xFFFFFFFF;
        private float alpha = 1.0f;
        private float emissiveStrength = 0.0f;
        private float uvScrollU = 0.0f;
        private float uvScrollV = 0.0f;

        public Builder layerName(String layerName) {
            this.layerName = layerName;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder renderMode(ULayerRenderMode renderMode) {
            this.renderMode = renderMode;
            return this;
        }

        public Builder blendMode(ULayerBlendMode blendMode) {
            this.blendMode = blendMode;
            return this;
        }

        public Builder shaderProfile(UShaderProfile shaderProfile) {
            this.shaderProfile = shaderProfile;
            return this;
        }

        public Builder shaderPass(UShaderPass shaderPass) {
            this.shaderPass = shaderPass;
            return this;
        }

        public Builder texture(UMaterialTextureSlot slot, UTextureBinding binding) {
            this.textures.put(slot, binding);
            return this;
        }

        public Builder features(Set<UShaderFeature> features) {
            this.features.addAll(features);
            return this;
        }

        public Builder resolvedParams(UMaterialParamSet resolvedParams) {
            this.resolvedParams = resolvedParams.copy();
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder emissiveStrength(float emissiveStrength) {
            this.emissiveStrength = emissiveStrength;
            return this;
        }

        public Builder uvScrollU(float uvScrollU) {
            this.uvScrollU = uvScrollU;
            return this;
        }

        public Builder uvScrollV(float uvScrollV) {
            this.uvScrollV = uvScrollV;
            return this;
        }

        public UMaterialRenderData build() {
            return new UMaterialRenderData(this);
        }
    }
}