package net.ironedge.libraryofiron.event.events;

import net.ironedge.libraryofiron.event.LoIEvent;
import net.minecraft.world.entity.LivingEntity;

public class ComboCompletedEvent extends LoIEvent {
    private final LivingEntity attacker;
    private final String comboId;
    private final int hits;
    public ComboCompletedEvent(LivingEntity attacker, String comboId, int hits) { this.attacker = attacker; this.comboId = comboId; this.hits = hits; }
    public LivingEntity getAttacker() { return attacker; }
    public String getComboId() { return comboId; }
    public int getHits() { return hits; }
}