package net.ironedge.libraryofiron.render.segmented;

import net.ironedge.libraryofiron.render.umar.material.UMaterialDefinition;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SegmentVariantDef {

    private final String id;
    private final ResourceLocation geoJsonId;
    private final UMaterialDefinition material;
    private final float length;

    private final Vector3f localOffset;
    private final Quaternionf localRotation;
    private final Vector3f localScale;

    public SegmentVariantDef(
            String id,
            ResourceLocation geoJsonId,
            UMaterialDefinition material,
            float length,
            Vector3f localOffset,
            Quaternionf localRotation,
            Vector3f localScale
    ) {
        this.id = id;
        this.geoJsonId = geoJsonId;
        this.material = material;
        this.length = length;
        this.localOffset = new Vector3f(localOffset);
        this.localRotation = new Quaternionf(localRotation);
        this.localScale = new Vector3f(localScale);
    }

    public String id() { return id; }
    public ResourceLocation geoJsonId() { return geoJsonId; }
    public UMaterialDefinition material() { return material; }
    public float length() { return length; }

    public Vector3f localOffset() { return new Vector3f(localOffset); }
    public Quaternionf localRotation() { return new Quaternionf(localRotation); }
    public Vector3f localScale() { return new Vector3f(localScale); }
}