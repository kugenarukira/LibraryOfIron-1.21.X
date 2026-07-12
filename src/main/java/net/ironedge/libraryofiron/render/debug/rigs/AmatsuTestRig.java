package net.ironedge.libraryofiron.render.debug.rigs;

import net.ironedge.libraryofiron.render.anchor.AnchorKey;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.segmented.SegmentVariantDef;
import net.ironedge.libraryofiron.render.segmented.SegmentVariantSelector;
import net.ironedge.libraryofiron.render.segmented.SegmentedRig;
import net.ironedge.libraryofiron.render.umar.material.UMaterialBuiltins;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class AmatsuTestRig implements SegmentedRig {

    private static final String MODID = "libraryofiron";

    private final SegmentVariantDef base;
    private final SegmentVariantDef length;
    private final SegmentVariantDef tip;
    private final SegmentVariantSelector selector;

    public AmatsuTestRig() {
        UMaterialDefinition sharedMat = UMaterialBuiltins.simpleGeo(
                ResourceLocation.fromNamespaceAndPath(MODID, "amatsuquiz")
        );

        this.base = new SegmentVariantDef(
                "base",
                ResourceLocation.fromNamespaceAndPath(MODID, "models/geo/amatsuquizbase.geo.json"),
                sharedMat,
                7.825f / 16.0f,
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1)
        );

        this.length = new SegmentVariantDef(
                "length",
                ResourceLocation.fromNamespaceAndPath(MODID, "models/geo/amatsuquizlength.geo.json"),
                sharedMat,
                7.625f / 16.0f,
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1)
        );

        this.tip = new SegmentVariantDef(
                "tip",
                ResourceLocation.fromNamespaceAndPath(MODID, "models/geo/amatsuquiztip.geo.json"),
                sharedMat,
                9.325f / 16.0f,
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1)
        );

        this.selector = (index, total) -> {
            if (index == 0) return base;
            if (index == total - 1) return tip;
            return length;
        };
    }

    @Override
    public boolean isActive(FrameContext frame) {
        Player p = Minecraft.getInstance().player;
        if (p == null) return false;
        return p.getMainHandItem().is(Items.RED_DYE) || p.getOffhandItem().is(Items.RED_DYE);
    }

    @Override
    public AnchorKey rootAnchor(FrameContext frame) {
        return net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys.HAND_R;
    }

    @Override
    public Vector3f rootOffset(FrameContext frame) {
        return new Vector3f();
    }

    @Override
    public Quaternionf rootRotation(FrameContext frame) {
        return new Quaternionf();
    }

    @Override
    public int segmentCount(FrameContext frame) {
        return 9;
    }

    @Override
    public AnchorKey segmentAnchorKey(int segmentIndex) {
        return new AnchorKey("amatsuquiz_s" + segmentIndex);
    }

    @Override
    public SegmentVariantSelector selector() {
        return selector;
    }
}