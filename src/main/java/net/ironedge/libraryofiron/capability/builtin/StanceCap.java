package net.ironedge.libraryofiron.capability.builtin;

import net.ironedge.libraryofiron.capability.LoICapability;
import net.ironedge.libraryofiron.core.LoIContext;

import java.util.HashMap;
import java.util.Map;

public final class StanceCap implements LoICapability<StanceCap> {

    private Stance current;
    private final Map<String, Stance> availableStances = new HashMap<>();

    public StanceCap() {
        this.current = null;
    }

    @Override
    public void onAttach(LoIContext context, Object holder) {}

    @Override
    public void tick(LoIContext context, Object holder) {
        // Optional: auto-recovery timers or stance effects per tick
    }

    public void registerStance(Stance stance) {
        availableStances.put(stance.getId(), stance);
        if (current == null) current = stance; // default first stance
    }

    public Stance getCurrent() {
        return current;
    }

    public boolean switchStance(String id) {
        Stance next = availableStances.get(id);
        if (next == null) return false;

        current = next;
        return true;
    }

    public Map<String, Stance> getAvailableStances() {
        return availableStances;
    }

    @Override
    public StanceCap getData() {
        return this;
    }

    /** Simple stance definition */
    public static class Stance {
        private final String id;
        private final String name;
        private final float damageMultiplier;
        private final float postureMultiplier;

        public Stance(String id, String name, float damageMultiplier, float postureMultiplier) {
            this.id = id;
            this.name = name;
            this.damageMultiplier = damageMultiplier;
            this.postureMultiplier = postureMultiplier;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public float getDamageMultiplier() { return damageMultiplier; }
        public float getPostureMultiplier() { return postureMultiplier; }
    }
}
