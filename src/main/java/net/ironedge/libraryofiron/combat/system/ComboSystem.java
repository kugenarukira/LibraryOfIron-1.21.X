package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.events.SpecialMoveEvent;
import net.ironedge.libraryofiron.core.LoICore;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public final class ComboSystem {

    private final Map<LivingEntity, Map<String, Integer>> entityCombos = new HashMap<>();
    private final Map<String, Integer> specialThresholds = new HashMap<>();

    public ComboSystem() {
        specialThresholds.put("combo_light", 3);
        specialThresholds.put("combo_heavy", 5);
    }

    private Map<String, Integer> getComboMap(LivingEntity entity) {
        return entityCombos.computeIfAbsent(entity, e -> new HashMap<>());
    }

    public void registerHit(LivingEntity attacker, String comboId) {
        Map<String, Integer> combos = getComboMap(attacker);
        combos.put(comboId, combos.getOrDefault(comboId, 0) + 1);
    }

    public boolean hasReachedSpecialThreshold(LivingEntity attacker, String comboId) {
        int hits = getComboMap(attacker).getOrDefault(comboId, 0);
        return hits >= specialThresholds.getOrDefault(comboId, Integer.MAX_VALUE);
    }

    public void triggerSpecialMove(
            LivingEntity attacker,
            LivingEntity target,
            String comboId
    ) {
        if (!hasReachedSpecialThreshold(attacker, comboId)) return;

        LoICore.context().getEventBus()
                .fire(new SpecialMoveEvent(attacker, comboId, target));

        resetCombo(attacker, comboId);
    }

    public void resetCombo(LivingEntity attacker, String comboId) {
        getComboMap(attacker).put(comboId, 0);
    }
}