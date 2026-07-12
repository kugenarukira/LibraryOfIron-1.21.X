package net.ironedge.libraryofiron.render.umar.texture;

public final class UTextureTransform {

    public static final UTextureTransform IDENTITY = new UTextureTransform(
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f
    );

    private final float uOffset;
    private final float vOffset;
    private final float uScale;
    private final float vScale;
    private final float rotationDegrees;

    public UTextureTransform(float uOffset, float vOffset, float uScale, float vScale, float rotationDegrees) {
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uScale = uScale;
        this.vScale = vScale;
        this.rotationDegrees = rotationDegrees;
    }

    public float uOffset() {
        return uOffset;
    }

    public float vOffset() {
        return vOffset;
    }

    public float uScale() {
        return uScale;
    }

    public float vScale() {
        return vScale;
    }

    public float rotationDegrees() {
        return rotationDegrees;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float uOffset = 0.0f;
        private float vOffset = 0.0f;
        private float uScale = 1.0f;
        private float vScale = 1.0f;
        private float rotationDegrees = 0.0f;

        public Builder uOffset(float uOffset) {
            this.uOffset = uOffset;
            return this;
        }

        public Builder vOffset(float vOffset) {
            this.vOffset = vOffset;
            return this;
        }

        public Builder uScale(float uScale) {
            this.uScale = uScale;
            return this;
        }

        public Builder vScale(float vScale) {
            this.vScale = vScale;
            return this;
        }

        public Builder rotationDegrees(float rotationDegrees) {
            this.rotationDegrees = rotationDegrees;
            return this;
        }

        public UTextureTransform build() {
            return new UTextureTransform(uOffset, vOffset, uScale, vScale, rotationDegrees);
        }
    }
}