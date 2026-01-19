package net.ironedge.libraryofiron.render.model.registry;

import net.ironedge.libraryofiron.render.model.part.ConditionContext;
import net.ironedge.libraryofiron.render.model.part.PartDef;
import net.ironedge.libraryofiron.render.model.part.PartInstance;
import net.ironedge.libraryofiron.render.model.render.PartRenderer;
import net.minecraft.world.entity.Entity;

import java.util.*;

public final class PartRegistry {

    private final Map<String, PartDef> defs = new LinkedHashMap<>();
    private final Map<String, PartRenderer> renderers = new HashMap<>();

    public void register(PartDef def, PartRenderer renderer) {
        defs.put(def.id(), def);
        renderers.put(def.id(), renderer);
    }

    public List<PartInstance> getPartsFor(Entity owner) {
        List<PartInstance> out = new ArrayList<>();
        for (PartDef def : defs.values()) {
            out.add(new PartInstance(def, owner));
        }
        return out;
    }

    public boolean shouldRender(PartInstance part, ConditionContext ctx) {
        return true; // only ALWAYS for now
    }

    public PartRenderer rendererFor(PartInstance part) {
        return renderers.get(part.def().id());
    }
}
