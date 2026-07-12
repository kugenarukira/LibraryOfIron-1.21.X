package net.ironedge.libraryofiron.render.umar.state;

import org.joml.Vector3f;

public final class UMaterialStateContext {

    private final float ageInTicks;
    private final float partialTick;

    private final float healthPercent;
    private final float movementSpeed;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float powerLevel;

    private final boolean hurt;
    private final boolean sprinting;
    private final boolean airborne;
    private final boolean glowing;

    private final Vector3f velocity;
    private final Vector3f cameraDirection;

    private UMaterialStateContext(Builder builder) {
        this.ageInTicks = builder.ageInTicks;
        this.partialTick = builder.partialTick;
        this.healthPercent = builder.healthPercent;
        this.movementSpeed = builder.movementSpeed;
        this.limbSwing = builder.limbSwing;
        this.limbSwingAmount = builder.limbSwingAmount;
        this.powerLevel = builder.powerLevel;
        this.hurt = builder.hurt;
        this.sprinting = builder.sprinting;
        this.airborne = builder.airborne;
        this.glowing = builder.glowing;
        this.velocity = new Vector3f(builder.velocity);
        this.cameraDirection = new Vector3f(builder.cameraDirection);
    }

    public float ageInTicks() { return ageInTicks; }
    public float partialTick() { return partialTick; }
    public float healthPercent() { return healthPercent; }
    public float movementSpeed() { return movementSpeed; }
    public float limbSwing() { return limbSwing; }
    public float limbSwingAmount() { return limbSwingAmount; }
    public float powerLevel() { return powerLevel; }

    public boolean hurt() { return hurt; }
    public boolean sprinting() { return sprinting; }
    public boolean airborne() { return airborne; }
    public boolean glowing() { return glowing; }

    public Vector3f velocity() { return new Vector3f(velocity); }
    public Vector3f cameraDirection() { return new Vector3f(cameraDirection); }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float ageInTicks = 0.0f;
        private float partialTick = 0.0f;

        private float healthPercent = 1.0f;
        private float movementSpeed = 0.0f;
        private float limbSwing = 0.0f;
        private float limbSwingAmount = 0.0f;
        private float powerLevel = 0.0f;

        private boolean hurt = false;
        private boolean sprinting = false;
        private boolean airborne = false;
        private boolean glowing = false;

        private Vector3f velocity = new Vector3f();
        private Vector3f cameraDirection = new Vector3f(0, 0, 1);

        public Builder ageInTicks(float ageInTicks) {
            this.ageInTicks = ageInTicks;
            return this;
        }

        public Builder partialTick(float partialTick) {
            this.partialTick = partialTick;
            return this;
        }

        public Builder healthPercent(float healthPercent) {
            this.healthPercent = clamp01(healthPercent);
            return this;
        }

        public Builder movementSpeed(float movementSpeed) {
            this.movementSpeed = Math.max(0.0f, movementSpeed);
            return this;
        }

        public Builder limbSwing(float limbSwing) {
            this.limbSwing = limbSwing;
            return this;
        }

        public Builder limbSwingAmount(float limbSwingAmount) {
            this.limbSwingAmount = Math.max(0.0f, limbSwingAmount);
            return this;
        }

        public Builder powerLevel(float powerLevel) {
            this.powerLevel = clamp01(powerLevel);
            return this;
        }

        public Builder hurt(boolean hurt) {
            this.hurt = hurt;
            return this;
        }

        public Builder sprinting(boolean sprinting) {
            this.sprinting = sprinting;
            return this;
        }

        public Builder airborne(boolean airborne) {
            this.airborne = airborne;
            return this;
        }

        public Builder glowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder velocity(Vector3f velocity) {
            this.velocity = velocity != null ? new Vector3f(velocity) : new Vector3f();
            return this;
        }

        public Builder cameraDirection(Vector3f cameraDirection) {
            this.cameraDirection = cameraDirection != null ? new Vector3f(cameraDirection) : new Vector3f(0, 0, 1);
            return this;
        }

        public UMaterialStateContext build() {
            return new UMaterialStateContext(this);
        }

        private static float clamp01(float value) {
            return Math.max(0.0f, Math.min(1.0f, value));
        }
    }
}