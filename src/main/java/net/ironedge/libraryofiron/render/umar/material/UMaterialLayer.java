package net.ironedge.libraryofiron.render.umar.material;

import net.ironedge.libraryofiron.render.umar.layer.ULayerBlendMode;
import net.ironedge.libraryofiron.render.umar.layer.ULayerRenderMode;
import net.ironedge.libraryofiron.render.umar.shader.UShaderFeature;
import net.ironedge.libraryofiron.render.umar.state.UMaterialDriver;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateRule;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class UMaterialLayer {

    private final String name;
    private final int priority;
    private final boolean enabledByDefault;

    private final ULayerRenderMode renderMode;
    private final ULayerBlendMode blendMode;

    private final Map<UMaterialTextureSlot, UTextureBinding> textures;
    private final Set<UShaderFeature> features;

    private final int defaultTintColor;
    private final float defaultAlpha;
    private final float defaultEmissiveStrength;

    private final UMaterialParamSet defaultParams;

    private final List<UMaterialDriver> drivers;
    private final List<UMaterialStateRule> rules;

    private UMaterialLayer(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.priority = builder.priority;
        this.enabledByDefault = builder.enabledByDefault;
        this.renderMode = Objects.requireNonNull(builder.renderMode, "renderMode");
        this.blendMode = Objects.requireNonNull(builder.blendMode, "blendMode");
        this.textures = Collections.unmodifiableMap(new EnumMap<>(builder.textures));
        this.features = Collections.unmodifiableSet(builder.features.isEmpty()
                ? EnumSet.noneOf(UShaderFeature.class)
                : EnumSet.copyOf(builder.features));
        this.defaultTintColor = builder.defaultTintColor;
        this.defaultAlpha = builder.defaultAlpha;
        this.defaultEmissiveStrength = builder.defaultEmissiveStrength;
        this.defaultParams = builder.defaultParams.copy();
        this.drivers = List.copyOf(builder.drivers);
        this.rules = List.copyOf(builder.rules);
    }

    public String name() {
        return name;
    }

    public int priority() {
        return priority;
    }

    public boolean enabledByDefault() {
        return enabledByDefault;
    }

    public ULayerRenderMode renderMode() {
        return renderMode;
    }

    public ULayerBlendMode blendMode() {
        return blendMode;
    }

    public Map<UMaterialTextureSlot, UTextureBinding> textures() {
        return textures;
    }

    public boolean hasTexture(UMaterialTextureSlot slot) {
        return textures.containsKey(slot);
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

    public int defaultTintColor() {
        return defaultTintColor;
    }

    public float defaultAlpha() {
        return defaultAlpha;
    }

    public float defaultEmissiveStrength() {
        return defaultEmissiveStrength;
    }

    public UMaterialParamSet defaultParams() {
        return defaultParams.copy();
    }

    public List<UMaterialDriver> drivers() {
        return drivers;
    }

    public List<UMaterialStateRule> rules() {
        return rules;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private int priority = 0;
        private boolean enabledByDefault = true;

        private ULayerRenderMode renderMode = ULayerRenderMode.OPAQUE;
        private ULayerBlendMode blendMode = ULayerBlendMode.NORMAL;

        private final Map<UMaterialTextureSlot, UTextureBinding> textures = new EnumMap<>(UMaterialTextureSlot.class);
        private final Set<UShaderFeature> features = EnumSet.noneOf(UShaderFeature.class);

        private int defaultTintColor = 0xFFFFFFFF;
        private float defaultAlpha = 1.0f;
        private float defaultEmissiveStrength = 0.0f;

        private UMaterialParamSet defaultParams = new UMaterialParamSet();

        private final List<UMaterialDriver> drivers = new ArrayList<>();
        private final List<UMaterialStateRule> rules = new ArrayList<>();

        public Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder enabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
            return this;
        }

        public Builder renderMode(ULayerRenderMode renderMode) {
            this.renderMode = Objects.requireNonNull(renderMode, "renderMode");
            return this;
        }

        public Builder blendMode(ULayerBlendMode blendMode) {
            this.blendMode = Objects.requireNonNull(blendMode, "blendMode");
            return this;
        }

        public Builder texture(UMaterialTextureSlot slot, UTextureBinding binding) {
            this.textures.put(Objects.requireNonNull(slot, "slot"), Objects.requireNonNull(binding, "binding"));
            return this;
        }

        public Builder feature(UShaderFeature feature) {
            this.features.add(Objects.requireNonNull(feature, "feature"));
            return this;
        }

        public Builder features(Set<UShaderFeature> features) {
            this.features.addAll(Objects.requireNonNull(features, "features"));
            return this;
        }

        public Builder defaultTintColor(int defaultTintColor) {
            this.defaultTintColor = defaultTintColor;
            return this;
        }

        public Builder defaultAlpha(float defaultAlpha) {
            this.defaultAlpha = defaultAlpha;
            return this;
        }

        public Builder defaultEmissiveStrength(float defaultEmissiveStrength) {
            this.defaultEmissiveStrength = defaultEmissiveStrength;
            return this;
        }

        public Builder defaultParams(UMaterialParamSet defaultParams) {
            this.defaultParams = Objects.requireNonNull(defaultParams, "defaultParams").copy();
            return this;
        }

        public Builder driver(UMaterialDriver driver) {
            this.drivers.add(Objects.requireNonNull(driver, "driver"));
            return this;
        }

        public Builder rule(UMaterialStateRule rule) {
            this.rules.add(Objects.requireNonNull(rule, "rule"));
            return this;
        }

        public UMaterialLayer build() {
            return new UMaterialLayer(this);
        }
    }
}