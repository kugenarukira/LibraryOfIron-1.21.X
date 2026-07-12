package net.ironedge.libraryofiron.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPerspectiveMode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.pose.PoseTransform;
import net.ironedge.libraryofiron.render.umar.material.UMaterialInstance;
import net.ironedge.libraryofiron.render.umar.material.UMaterialParams;
import net.ironedge.libraryofiron.render.umar.material.UMaterialPassPlan;
import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderData;
import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderTypes;
import net.ironedge.libraryofiron.render.umar.material.UMaterialRenderUtil;
import net.ironedge.libraryofiron.render.umar.material.UMaterialResolver;
import net.ironedge.libraryofiron.render.umar.material.UMaterialTextureSlot;
import net.ironedge.libraryofiron.render.umar.shader.UShaderPass;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import net.ironedge.libraryofiron.render.umr.geo.GeoMasks;
import net.ironedge.libraryofiron.render.umr.geo.GeoMesh;
import net.ironedge.libraryofiron.render.umr.geo.GeoMeshRegistry;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineGroupDef;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineGrouping;
import net.ironedge.libraryofiron.render.umr.geo.GeoRenderMask;
import net.ironedge.libraryofiron.render.umr.geo.GeoRig;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineFace;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineProxyBuilder;
import net.ironedge.libraryofiron.render.umr.geo.GeoOutlineProxyMesh;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GeoDebugRenderNode extends RenderNode {

    private final ResourceLocation geoJsonId;
    private final UMaterialInstance material;
    private final GeoRig rig;
    private final RenderPerspectiveMode renderMode;
    private final UMaterialResolver resolver = new UMaterialResolver();

    public GeoDebugRenderNode(
            ResourceLocation geoJsonId,
            UMaterialInstance material,
            GeoRig rig,
            RenderPerspectiveMode renderMode
    ) {
        super(RenderPhase.DEBUG);
        this.geoJsonId = geoJsonId;
        this.material = material;
        this.rig = rig;
        this.renderMode = renderMode;
    }

    @Override
    public void render(FrameContext frame) {
        boolean firstPerson = net.ironedge.libraryofiron.render.core.PerspectiveUtil.isFirstPerson();

        if (renderMode == RenderPerspectiveMode.HIDDEN) return;
        if (renderMode == RenderPerspectiveMode.FIRST_PERSON_ONLY && !firstPerson) return;
        if (renderMode == RenderPerspectiveMode.THIRD_PERSON_ONLY && firstPerson) return;

        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        GeoMesh mesh = GeoMeshRegistry.get(geoJsonId);
        if (mesh == null) return;

        Map<String, PoseTransform> worldByBone = new HashMap<>();

        var mc = Minecraft.getInstance();
        var buffers = mc.renderBuffers().bufferSource();

        UMaterialStateContext stateContext = UMaterialStateContext.builder()
                .ageInTicks(mc.level != null ? mc.level.getGameTime() : 0.0f)
                .partialTick(frame.partialTicks())
                .healthPercent(1.0f)
                .movementSpeed(0.0f)
                .hurt(false)
                .sprinting(false)
                .airborne(false)
                .build();

        UMaterialPassPlan passPlan = resolver.resolve(material, stateContext);
        if (passPlan.isEmpty()) return;

        List<UMaterialRenderData> passes = new ArrayList<>(passPlan.passes());
        passes.sort((a, b) -> {
            if (a.shaderPass() == b.shaderPass()) return 0;
            if (a.shaderPass() == UShaderPass.OUTLINE) return 1;
            if (b.shaderPass() == UShaderPass.OUTLINE) return -1;
            return 0;
        });

        Vector3f cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        var bindings = rig.bindings();
        String poseSpaceId = rig.poseSpaceId();

        Integer maskObj = frame.attachment("geoMask", Integer.class);
        int activeMask = (maskObj != null) ? maskObj : GeoRenderMask.DEFAULT.bit;

        var outlineGroups = GeoOutlineGrouping.resolveGroups(rig, mesh);

        var hidden = frame.attachment("geoHiddenBones", java.util.Set.class);
        var only = frame.attachment("geoOnlyBones", java.util.Set.class);

        for (UMaterialRenderData pass : passes) {
            ResourceLocation texture = resolvePrimaryTexture(pass);
            if (texture == null) continue;

            RenderType renderType = UMaterialRenderTypes.forPass(pass, texture);
            if (renderType == null) continue;

            VertexConsumer vc = buffers.getBuffer(renderType);

            int argb = pass.color();
            int a = UMaterialRenderUtil.colorA(argb, pass.alpha());
            int r = UMaterialRenderUtil.colorR(argb);
            int g = UMaterialRenderUtil.colorG(argb);
            int b = UMaterialRenderUtil.colorB(argb);

            if (pass.shaderPass() == UShaderPass.OUTLINE) {
                a = 255;
                r = 0;
                g = 0;
                b = 0;
            }

            boolean isOutline = pass.shaderPass() == UShaderPass.OUTLINE;

            if (!isOutline) {
                for (var e : mesh.bones.entrySet()) {
                    String boneName = e.getKey();
                    GeoMesh.BoneMesh bm = e.getValue();

                    bm.visible = rig.visible(boneName, firstPerson);
                    bm.mask = rig.mask(boneName);

                    if (!bm.visible) continue;
                    if (hidden != null && hidden.contains(boneName)) continue;
                    if (only != null && !only.contains(boneName)) continue;
                    if (!GeoMasks.matches(bm.mask, activeMask)) continue;

                    PoseTransform world = resolveBoneWorld(
                            frame,
                            boneName,
                            mesh,
                            bindings,
                            poseSpaceId,
                            worldByBone
                    );
                    if (world == null) continue;

                    ps.pushPose();
                    ps.translate(world.translation().x, world.translation().y, world.translation().z);
                    ps.mulPose(world.rotation());
                    ps.scale(world.scale().x, world.scale().y, world.scale().z);

                    drawBoneMesh(vc, ps, bm, pass, r, g, b, a);

                    ps.popPose();
                }
            } else {
                float baseOutlineWidth = pass.resolvedParams()
                        .getFloat(UMaterialParams.OUTLINE_WIDTH, 0.01f);

                for (GeoOutlineGroupDef group : outlineGroups) {
                    if (!group.enabled()) continue;

                    PoseTransform rootWorld = resolveBoneWorld(
                            frame,
                            group.rootBone(),
                            mesh,
                            bindings,
                            poseSpaceId,
                            worldByBone
                    );
                    if (rootWorld == null) continue;

                    boolean groupVisible = false;

                    for (String boneName : group.memberBones()) {
                        GeoMesh.BoneMesh bm = mesh.bones.get(boneName);
                        if (bm == null) continue;

                        bm.visible = rig.visible(boneName, firstPerson);
                        bm.mask = rig.mask(boneName);

                        if (!bm.visible) continue;
                        if (hidden != null && hidden.contains(boneName)) continue;
                        if (only != null && !only.contains(boneName)) continue;
                        if (!GeoMasks.matches(bm.mask, activeMask)) continue;

                        PoseTransform memberWorld = resolveBoneWorld(
                                frame,
                                boneName,
                                mesh,
                                bindings,
                                poseSpaceId,
                                worldByBone
                        );
                        if (memberWorld == null) continue;

                        groupVisible = true;
                    }

                    if (!groupVisible) continue;

                    GeoOutlineProxyMesh proxy = GeoOutlineProxyBuilder.buildProxy(
                            mesh,
                            group
                    );
                    if (proxy.isEmpty()) continue;

                    float outlineScale = 1.0f + (baseOutlineWidth * group.widthMultiplier());

                    ps.pushPose();
                    ps.translate(rootWorld.translation().x, rootWorld.translation().y, rootWorld.translation().z);
                    ps.mulPose(rootWorld.rotation());
                    ps.scale(rootWorld.scale().x, rootWorld.scale().y, rootWorld.scale().z);
                    ps.scale(outlineScale, outlineScale, outlineScale);

                    drawOutlineProxyMesh(vc, ps, proxy, r, g, b, a);

                    ps.popPose();
                }
            }

            buffers.endBatch(renderType);
        }

        ps.popPose();
    }

    private ResourceLocation resolvePrimaryTexture(UMaterialRenderData pass) {
        if (pass.texture(UMaterialTextureSlot.BASE) != null) {
            return pass.texture(UMaterialTextureSlot.BASE).texture();
        }
        if (pass.texture(UMaterialTextureSlot.EMISSIVE) != null) {
            return pass.texture(UMaterialTextureSlot.EMISSIVE).texture();
        }
        if (pass.texture(UMaterialTextureSlot.OVERLAY) != null) {
            return pass.texture(UMaterialTextureSlot.OVERLAY).texture();
        }
        if (pass.texture(UMaterialTextureSlot.NOISE) != null) {
            return pass.texture(UMaterialTextureSlot.NOISE).texture();
        }
        return null;
    }

    private PoseTransform resolveBoneWorld(
            FrameContext frame,
            String boneName,
            GeoMesh mesh,
            Map<String, net.ironedge.libraryofiron.render.umr.geo.GeoBinding> bindings,
            String poseSpaceId,
            Map<String, PoseTransform> cache
    ) {
        PoseTransform cached = cache.get(boneName);
        if (cached != null) return cached;

        GeoMesh.BoneMesh bm = mesh.bones.get(boneName);
        if (bm == null) return null;

        var bind = bindings.get(boneName);
        if (bind == null) {
            bind = net.ironedge.libraryofiron.render.umr.geo.GeoBinding.inherit();
        }

        if (bind.anchor() != null) {
            var cameraEntity = frame.attachment("cameraEntity", net.minecraft.world.entity.Entity.class);
            var arc = new net.ironedge.libraryofiron.render.anchor.resolve.AnchorResolutionContext(
                    cameraEntity,
                    frame.partialTicks()
            );

            var ap = net.ironedge.libraryofiron.render.anchor.resolve.AnchorWorldResolver.resolve(
                    poseSpaceId,
                    bind.anchor(),
                    rig.anchorMap(),
                    arc
            );

            if (ap == null) return null;

            var worldRot = new Quaternionf(ap.rot())
                    .mul(bm.restRotMc)
                    .mul(bind.localRot());

            var worldPos = new Vector3f(ap.pos())
                    .add(new Vector3f(bind.localOffset()).rotate(worldRot));

            var worldScale = new Vector3f(ap.scale()).mul(bind.localScale());

            PoseTransform out = new PoseTransform(worldPos, worldRot, worldScale);
            cache.put(boneName, out);
            return out;
        }

        if (bm.parent != null) {
            PoseTransform parentWorld = resolveBoneWorld(frame, bm.parent, mesh, bindings, poseSpaceId, cache);
            if (parentWorld == null) return null;

            GeoMesh.BoneMesh parentBm = mesh.bones.get(bm.parent);
            if (parentBm == null) return null;

            Quaternionf worldRot = new Quaternionf(parentWorld.rotation())
                    .mul(bm.restRotMc)
                    .mul(bind.localRot());

            Vector3f restOffset = new Vector3f(bm.pivotMc).sub(parentBm.pivotMc);
            restOffset.mul(parentWorld.scale());
            restOffset.rotate(parentWorld.rotation());

            Vector3f worldPos = new Vector3f(parentWorld.translation()).add(restOffset);

            Vector3f localOff = new Vector3f(bind.localOffset()).mul(parentWorld.scale());
            worldPos.add(localOff.rotate(worldRot));

            Vector3f worldScale = new Vector3f(parentWorld.scale()).mul(bind.localScale());

            PoseTransform out = new PoseTransform(worldPos, worldRot, worldScale);
            cache.put(boneName, out);
            return out;
        }

        return null;
    }

    private static Matrix4f memberToGroupMatrix(PoseTransform groupRootWorld, PoseTransform memberWorld) {
        Matrix4f rootWorld = compose(groupRootWorld);
        Matrix4f rootWorldInv = new Matrix4f(rootWorld).invert();
        Matrix4f memberWorldMat = compose(memberWorld);
        return rootWorldInv.mul(memberWorldMat);
    }

    private static Matrix4f compose(PoseTransform transform) {
        return new Matrix4f()
                .translationRotateScale(
                        transform.translation(),
                        transform.rotation(),
                        transform.scale()
                );
    }

    private static Matrix3f normalMatrix(Matrix4f transform) {
        return transform.normal(new Matrix3f());
    }

    private static void put(
            VertexConsumer vc,
            PoseStack.Pose pose,
            GeoMesh.Vertex v,
            Vector3f n,
            int light,
            int overlay,
            int r,
            int g,
            int b,
            int a
    ) {
        vc.addVertex(pose.pose(), v.pos.x, v.pos.y, v.pos.z)
                .setColor(r, g, b, a)
                .setUv(v.u, v.v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, n.x, n.y, n.z);
    }

    private static void putTransformed(
            VertexConsumer vc,
            PoseStack.Pose pose,
            GeoMesh.Vertex v,
            Vector3f n,
            int light,
            int overlay,
            int r,
            int g,
            int b,
            int a,
            Matrix4f transform,
            Matrix3f normalTransform
    ) {
        Vector4f p = new Vector4f(v.pos.x, v.pos.y, v.pos.z, 1.0f).mul(transform);
        Vector3f nn = new Vector3f(n).mul(normalTransform).normalize();

        vc.addVertex(pose.pose(), p.x, p.y, p.z)
                .setColor(r, g, b, a)
                .setUv(v.u, v.v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nn.x, nn.y, nn.z);
    }

    private static void drawBoneMesh(
            VertexConsumer vc,
            PoseStack ps,
            GeoMesh.BoneMesh bm,
            UMaterialRenderData pass,
            int r,
            int g,
            int b,
            int a
    ) {
        PoseStack.Pose pose = ps.last();
        int light = LightTexture.FULL_BRIGHT;
        int overlay = OverlayTexture.NO_OVERLAY;

        boolean isOutline = pass.shaderPass() == UShaderPass.OUTLINE;

        for (GeoMesh.Quad q : bm.quads) {
            if (isOutline) {
                put(vc, pose, q.d, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.c, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.b, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.a, q.normal, light, overlay, r, g, b, a);
            } else {
                put(vc, pose, q.a, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.b, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.c, q.normal, light, overlay, r, g, b, a);
                put(vc, pose, q.d, q.normal, light, overlay, r, g, b, a);
            }
        }
    }

    private static void drawBoneMeshTransformed(
            VertexConsumer vc,
            PoseStack.Pose pose,
            GeoMesh.BoneMesh bm,
            UMaterialRenderData pass,
            int r,
            int g,
            int b,
            int a,
            Matrix4f transform,
            Matrix3f normalTransform
    ) {
        int light = LightTexture.FULL_BRIGHT;
        int overlay = OverlayTexture.NO_OVERLAY;

        boolean isOutline = pass.shaderPass() == UShaderPass.OUTLINE;

        for (GeoMesh.Quad q : bm.quads) {
            if (isOutline) {
                putTransformed(vc, pose, q.d, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.c, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.b, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.a, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
            } else {
                putTransformed(vc, pose, q.a, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.b, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.c, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
                putTransformed(vc, pose, q.d, q.normal, light, overlay, r, g, b, a, transform, normalTransform);
            }
        }
    }

    private static void drawOutlineProxyMesh(
            VertexConsumer vc,
            PoseStack ps,
            GeoOutlineProxyMesh proxy,
            int r,
            int g,
            int b,
            int a
    ) {
        PoseStack.Pose pose = ps.last();
        int light = LightTexture.FULL_BRIGHT;
        int overlay = OverlayTexture.NO_OVERLAY;

        for (GeoOutlineFace face : proxy.faces()) {
            // Reverse winding for outline shell so culling keeps the outer rim
            putFaceVertex(vc, pose, face.d(), face.uvD(), face.normal(), light, overlay, r, g, b, a);
            putFaceVertex(vc, pose, face.c(), face.uvC(), face.normal(), light, overlay, r, g, b, a);
            putFaceVertex(vc, pose, face.b(), face.uvB(), face.normal(), light, overlay, r, g, b, a);
            putFaceVertex(vc, pose, face.a(), face.uvA(), face.normal(), light, overlay, r, g, b, a);
        }
    }

    private static void putFaceVertex(
            VertexConsumer vc,
            PoseStack.Pose pose,
            Vector3f pos,
            org.joml.Vector2f uv,
            Vector3f normal,
            int light,
            int overlay,
            int r,
            int g,
            int b,
            int a
    ) {
        vc.addVertex(pose.pose(), pos.x, pos.y, pos.z)
                .setColor(r, g, b, a)
                .setUv(uv.x, uv.y)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, normal.x, normal.y, normal.z);
    }
}