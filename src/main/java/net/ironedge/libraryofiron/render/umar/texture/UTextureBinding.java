package net.ironedge.libraryofiron.render.umar.texture;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class UTextureBinding {

    private final ResourceLocation texture;
    private final UTextureTransform transform;
    private final UTextureAnimation animation;

    public UTextureBinding(ResourceLocation texture, UTextureTransform transform, UTextureAnimation animation) {
        this.texture = Objects.requireNonNull(texture, "texture");
        this.transform = Objects.requireNonNull(transform, "transform");
        this.animation = Objects.requireNonNull(animation, "animation");
    }

    public ResourceLocation texture() {
        return texture;
    }

    public UTextureTransform transform() {
        return transform;
    }

    public UTextureAnimation animation() {
        return animation;
    }

    public static UTextureBinding of(ResourceLocation texture) {
        return new UTextureBinding(texture, UTextureTransform.IDENTITY, UTextureAnimation.NONE);
    }

    public static Builder builder(ResourceLocation texture) {
        return new Builder(texture);
    }

    public static final class Builder {
        private final ResourceLocation texture;
        private UTextureTransform transform = UTextureTransform.IDENTITY;
        private UTextureAnimation animation = UTextureAnimation.NONE;

        public Builder(ResourceLocation texture) {
            this.texture = Objects.requireNonNull(texture, "texture");
        }

        public Builder transform(UTextureTransform transform) {
            this.transform = Objects.requireNonNull(transform, "transform");
            return this;
        }

        public Builder animation(UTextureAnimation animation) {
            this.animation = Objects.requireNonNull(animation, "animation");
            return this;
        }

        public UTextureBinding build() {
            return new UTextureBinding(texture, transform, animation);
        }
    }
}