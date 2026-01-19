package net.ironedge.libraryofiron.render.model.part;

import net.minecraft.world.entity.Entity;

public record ConditionContext(
        Entity viewer,
        Entity target,
        boolean isGui
) {}
