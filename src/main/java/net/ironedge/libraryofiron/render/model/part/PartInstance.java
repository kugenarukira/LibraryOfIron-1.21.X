package net.ironedge.libraryofiron.render.model.part;

import net.minecraft.world.entity.Entity;

public final class PartInstance {
    private final PartDef def;
    private final Entity owner;

    public PartInstance(PartDef def, Entity owner) {
        this.def = def;
        this.owner = owner;
    }

    public PartDef def() { return def; }
    public Entity owner() { return owner; }
}
