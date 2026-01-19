package net.ironedge.libraryofiron.render.model.part;

public record PartDef(
        String id,
        PartType type,
        AttachRule attach,
        RenderCondition condition
) {}
