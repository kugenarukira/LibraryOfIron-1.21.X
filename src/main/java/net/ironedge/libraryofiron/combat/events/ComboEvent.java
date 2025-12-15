package net.ironedge.libraryofiron.combat.events;

import net.ironedge.libraryofiron.event.LoIEvent;

public class ComboEvent extends LoIEvent {
    private final String comboId;
    private final int hits;

    public ComboEvent(String comboId, int hits) {
        this.comboId = comboId;
        this.hits = hits;
    }

    public String getComboId() { return comboId; }
    public int getHits() { return hits; }
}