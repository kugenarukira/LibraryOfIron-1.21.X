package net.ironedge.libraryofiron.combat.targeting;

import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TargetResolver {

    public Optional<LivingEntity> getClosestTarget(LivingEntity seeker, List<LivingEntity> candidates, double maxDistance) {
        return candidates.stream()
                .filter(e -> !e.isDeadOrDying())
                .filter(e -> e.distanceTo(seeker) <= maxDistance)
                .min(Comparator.comparingDouble(a -> a.distanceTo(seeker)));
    }
}