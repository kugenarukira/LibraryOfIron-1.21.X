package net.ironedge.libraryofiron.render.segmented;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.anchor.preset.AnchorPoseQ;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.umar.material.UMaterialCompat;
import net.ironedge.libraryofiron.render.umr.geo.GeoMesh;
import net.ironedge.libraryofiron.render.umr.geo.GeoMeshRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SegmentedRenderNode extends RenderNode {

    private final SegmentedRig rig;

    public SegmentedRenderNode(SegmentedRig rig) {
        super(RenderPhase.DEBUG);
        this.rig = rig;
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;
        if (!rig.isActive(frame)) return;

        int totalSegments = rig.segmentCount(frame);
        if (totalSegments <= 0) return;

        AnchorPoseQ root = SegmentedRenderUtil.resolvePlayerRoot(
                rig.poseSpaceId(),
                rig.rootAnchor(frame)
        );
        if (root == null) {
            return;
        }

        var mc = Minecraft.getInstance();
        var buffers = mc.renderBuffers().bufferSource();
        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        // Root transform only used for local hand-side adjustment right now
        Vector3f rootPos = new Vector3f(root.pos());
        Quaternionf rootRot = new Quaternionf(root.rot()).mul(rig.rootRotation(frame));
        Vector3f rootOffsetWorld = new Vector3f(rig.rootOffset(frame)).rotate(rootRot);
        rootPos.add(rootOffsetWorld);

        for (int i = 0; i < totalSegments; i++) {
            var segKey = rig.segmentAnchorKey(i);
            AnchorPoseQ segAnchor = SegmentedRenderUtil.resolveDynamic(
                    () -> frame.partialTicks(),
                    segKey
            );
            if (segAnchor == null) continue;

            SegmentVariantDef variant = rig.selector().select(i, totalSegments);
            if (variant == null) continue;

            GeoMesh mesh = GeoMeshRegistry.get(variant.geoJsonId());
            if (mesh == null) continue;

            ResourceLocation baseTexture = UMaterialCompat.baseTexture(variant.material());
            if (baseTexture == null) continue;

            RenderType rt = RenderType.entityCutoutNoCull(baseTexture);
            VertexConsumer vc = buffers.getBuffer(rt);

            ps.pushPose();

            ps.translate(segAnchor.pos().x, segAnchor.pos().y, segAnchor.pos().z);
            ps.mulPose(segAnchor.rot());


            var matInstance = new net.ironedge.libraryofiron.render.umar.material.UMaterialInstance(variant.material());
            int[] rgba = net.ironedge.libraryofiron.render.umar.material.UMaterialRenderHelper.rgba(matInstance);

            // ONLY per-model authoring correction here
            ps.translate(
                    variant.localOffset().x,
                    variant.localOffset().y,
                    variant.localOffset().z
            );
            ps.mulPose(variant.localRotation());
            ps.scale(
                    variant.localScale().x,
                    variant.localScale().y,
                    variant.localScale().z
            );

            drawMesh(vc, ps, mesh, rgba);

            ps.popPose();
            buffers.endBatch(rt);
        }

        ps.popPose();
    }

    private static void drawMesh(VertexConsumer vc, PoseStack ps, GeoMesh mesh, int[] rgba) {
        PoseStack.Pose pose = ps.last();
        int light = LightTexture.FULL_BRIGHT;
        int overlay = OverlayTexture.NO_OVERLAY;

        for (GeoMesh.BoneMesh bm : mesh.bones.values()) {
            for (GeoMesh.Quad q : bm.quads) {
                put(vc, pose, q.a, q.normal, light, overlay, rgba);
                put(vc, pose, q.b, q.normal, light, overlay, rgba);
                put(vc, pose, q.c, q.normal, light, overlay, rgba);
                put(vc, pose, q.d, q.normal, light, overlay, rgba);
            }
        }
    }

    private static void put(VertexConsumer vc, PoseStack.Pose pose,
                            GeoMesh.Vertex v, Vector3f n, int light, int overlay,
                            int[] rgba) {
        vc.addVertex(pose.pose(), v.pos.x, v.pos.y, v.pos.z)
                .setColor(rgba[0], rgba[1], rgba[2], rgba[3])
                .setUv(v.u, v.v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, n.x, n.y, n.z);
    }
}