package net.ironedge.libraryofiron.render.anchor.resolve;

import net.minecraft.world.entity.Entity;

public final class AnchorResolutionContext {

    private final Entity entity;
    private final float partialTicks;

    public AnchorResolutionContext(Entity entity, float partialTicks) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }

    public Entity entity() {
        return entity;
    }

    public float partialTicks() {
        return partialTicks;
    }
}
