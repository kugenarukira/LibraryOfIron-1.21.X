package net.ironedge.libraryofiron.render.umar.texture;

public final class UTextureAnimation {

    public static final UTextureAnimation NONE = new UTextureAnimation(
            0.0f, 0.0f,
            1, 0.0f,
            true
    );

    private final float scrollUPerTick;
    private final float scrollVPerTick;
    private final int frameCount;
    private final float framesPerSecond;
    private final boolean loop;

    public UTextureAnimation(float scrollUPerTick, float scrollVPerTick, int frameCount, float framesPerSecond, boolean loop) {
        this.scrollUPerTick = scrollUPerTick;
        this.scrollVPerTick = scrollVPerTick;
        this.frameCount = Math.max(1, frameCount);
        this.framesPerSecond = Math.max(0.0f, framesPerSecond);
        this.loop = loop;
    }

    public float scrollUPerTick() {
        return scrollUPerTick;
    }

    public float scrollVPerTick() {
        return scrollVPerTick;
    }

    public int frameCount() {
        return frameCount;
    }

    public float framesPerSecond() {
        return framesPerSecond;
    }

    public boolean loop() {
        return loop;
    }

    public boolean hasScroll() {
        return scrollUPerTick != 0.0f || scrollVPerTick != 0.0f;
    }

    public boolean hasFrameAnimation() {
        return frameCount > 1 && framesPerSecond > 0.0f;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float scrollUPerTick = 0.0f;
        private float scrollVPerTick = 0.0f;
        private int frameCount = 1;
        private float framesPerSecond = 0.0f;
        private boolean loop = true;

        public Builder scrollUPerTick(float scrollUPerTick) {
            this.scrollUPerTick = scrollUPerTick;
            return this;
        }

        public Builder scrollVPerTick(float scrollVPerTick) {
            this.scrollVPerTick = scrollVPerTick;
            return this;
        }

        public Builder frameCount(int frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        public Builder framesPerSecond(float framesPerSecond) {
            this.framesPerSecond = framesPerSecond;
            return this;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public UTextureAnimation build() {
            return new UTextureAnimation(scrollUPerTick, scrollVPerTick, frameCount, framesPerSecond, loop);
        }
    }
}