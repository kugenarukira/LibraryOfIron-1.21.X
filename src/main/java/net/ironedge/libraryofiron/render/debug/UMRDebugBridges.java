package net.ironedge.libraryofiron.render.debug;

import net.ironedge.libraryofiron.render.anchor.preset.AnchorKeys;
import net.ironedge.libraryofiron.render.bridge.*;
import net.ironedge.libraryofiron.render.pose.PlayerAnchorMap;
import net.ironedge.libraryofiron.render.pose.PoseKey;
import net.ironedge.libraryofiron.render.umr.UMRModelDef;
import net.ironedge.libraryofiron.render.umr.UMRNodeDef;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public final class UMRDebugBridges {
    private UMRDebugBridges() {}

    public static void install() {
        BridgeSystem.get().add(new UMRBridgeInstance(testChain()));
    }

    private static UMRBridgeDef testChain() {
        // build variants
        UMRModelDef segShort = new UMRModelDef("seg_short");
        segShort.addNode(UMRNodeDef.root("root"));
        segShort.addNode(new UMRNodeDef("tip","root", new Vector3f(0,0,0.20f), new Quaternionf(), new Vector3f(1)));

        UMRModelDef segMed = new UMRModelDef("seg_med");
        segMed.addNode(UMRNodeDef.root("root"));
        segMed.addNode(new UMRNodeDef("tip","root", new Vector3f(0,0,0.30f), new Quaternionf(), new Vector3f(1)));

        UMRModelDef segLong = new UMRModelDef("seg_long");
        segLong.addNode(UMRNodeDef.root("root"));
        segLong.addNode(new UMRNodeDef("tip","root", new Vector3f(0,0,0.45f), new Quaternionf(), new Vector3f(1)));

        List<SegmentVariant> variants = List.of(
                new SegmentVariant("short", segShort, "root", "tip"),
                new SegmentVariant("med",   segMed,   "root", "tip"),
                new SegmentVariant("long",  segLong,  "root", "tip")
        );

        Endpoint start = new AnchorEndpoint("player", AnchorKeys.TORSO, PlayerAnchorMap.INSTANCE);
        Endpoint end   = new AnchorEndpoint("player", AnchorKeys.PHYSICS_CHAIN_TIP, null);

        /*Endpoint start = new PoseKeyEndpoint(new PoseKey("player", "Body"));

        Endpoint end = DebugEndpoints.playerForwardWithTipModes(
                new PoseKey("player","Body"),
                12,
                variants,
                TipDistanceMode.RANDOM_ONCE, // contracted
                TipDistanceMode.RANDOM_ONCE  // extended
        );*/


        return new UMRBridgeDef(
                "test_chain",
                start,
                end,
                variants,
                12,
                SegmentOrdering.ORGANIC,
                OrganicMotion.PING_PONG,
                TipDistanceMode.RANDOM_CONTINUOUS,
                TipDistanceMode.RANDOM_CONTINUOUS
        );

    }
}