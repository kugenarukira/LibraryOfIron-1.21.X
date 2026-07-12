package net.ironedge.libraryofiron.render.umr.mesh.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.umar.material.*;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.mesh.deform.SkeletalMeshDeformer;
import net.ironedge.libraryofiron.render.umr.morph.MorphBlender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MeshRenderNode extends RenderNode {

    private final MeshInstance instance;
    private final UMaterialInstance fallbackMaterial;
    private final Map<String, UMaterialInstance> materialsBySlot;
    private final UMaterialResolver resolver = new UMaterialResolver();

    public MeshRenderNode(MeshInstance instance, UMaterialInstance material) {
        this(instance, material, Map.of());
    }

    public MeshRenderNode(
            MeshInstance instance,
            UMaterialInstance fallbackMaterial,
            Map<String, UMaterialInstance> materialsBySlot
    ) {
        super(RenderPhase.DEBUG);
        this.instance = Objects.requireNonNull(instance, "instance");
        this.fallbackMaterial = Objects.requireNonNull(fallbackMaterial, "fallbackMaterial");
        this.materialsBySlot = materialsBySlot != null ? new HashMap<>(materialsBySlot) : new HashMap<>();
    }

    @Override
    public void render(FrameContext frame) {
        net.ironedge.libraryofiron.render.umr.importdata.fbx.FbxTextureResolver.flushPendingDynamicTextures();

        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        UMaterialStateContext stateContext = buildStateContext(frame);

        MeshAsset asset = instance.asset();
        var surfaces = asset.surfacesView();

        Map<String, float[]> blendedPositions = MorphBlender.blendPositions(
                asset,
                instance.morphState()
        );

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        Vector3f cam = frame.cameraPos();
        Vector3f t = instance.translation();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);
        ps.translate(t.x, t.y, t.z);
        ps.mulPose(instance.rotation());
        ps.scale(instance.scale().x, instance.scale().y, instance.scale().z);

        PoseStack.Pose pose = ps.last();
        int overlay = OverlayTexture.NO_OVERLAY;

        for (MeshSurface surface : surfaces) {
            UMaterialInstance surfaceMaterial = materialForSurface(surface);
            UMaterialPassPlan passPlan = resolver.resolve(surfaceMaterial, stateContext);

            if (passPlan.isEmpty()) continue;

            for (UMaterialRenderData pass : passPlan.passes()) {
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

                int light = LightTexture.FULL_BRIGHT; // TODO real packed light from frame/context
                UTextureBinding binding = resolveBinding(pass);



                float[] basePos = surface.positionsUnsafe();
                float[] blended = blendedPositions.get(surface.name());

                float[] pos = basePos;

                if (blended != null && basePos != null && blended.length == basePos.length) {
                    pos = blended;
                }

                float[] normals = surface.normalsUnsafe();

                pos = SkeletalMeshDeformer.deformPositions(instance, surface, pos);
                normals = SkeletalMeshDeformer.deformNormals(instance, surface, normals);

                int[] indices = surface.indicesUnsafe();
                if (pos == null || indices == null) continue;

                float[] uvs = surface.uvsUnsafe();



                for (int i = 0; i + 2 < indices.length; i += 3) {
                    emitVertex(vc, pose, pos, normals, uvs, indices[i], pass, binding, r, g, b, a, overlay, light);
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 1], pass, binding, r, g, b, a, overlay, light);
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 2], pass, binding, r, g, b, a, overlay, light);

                    // IMPORTANT:
                    // The current RenderType path consumes quads, so each imported triangle is padded
                    // into a degenerate quad. Removing this causes the "one triangle of the rectangle" bug.
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 2], pass, binding, r, g, b, a, overlay, light);
                }

                buffers.endBatch(renderType);
            }
        }

        ps.popPose();
    }

    private UMaterialInstance materialForSurface(MeshSurface surface) {
        if (surface == null || surface.materialSlot() == null) {
            return fallbackMaterial;
        }

        return materialsBySlot.getOrDefault(surface.materialSlot(), fallbackMaterial);
    }

    private UMaterialStateContext buildStateContext(FrameContext frame) {
        Minecraft mc = Minecraft.getInstance();
        float age = 0.0f;

        if (mc.level != null) {
            age = mc.level.getGameTime();
        }

        return UMaterialStateContext.builder()
                .ageInTicks(age)
                .partialTick(frame.partialTicks())
                .healthPercent(1.0f)
                .movementSpeed(0.0f)
                .hurt(false)
                .sprinting(false)
                .airborne(false)
                .build();
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

    private UTextureBinding resolveBinding(UMaterialRenderData pass) {
        var binding = pass.texture(UMaterialTextureSlot.BASE);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.EMISSIVE);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.OVERLAY);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.NOISE);
        return binding;
    }

    private static void emitVertex(
            VertexConsumer vc,
            PoseStack.Pose pose,
            float[] pos,
            float[] normals,
            float[] uvs,
            int vi,
            UMaterialRenderData pass,
            UTextureBinding binding,
            int r,
            int g,
            int b,
            int a,
            int overlay,
            int light
    ) {
        int pBase = vi * 3;

        if (pBase < 0 || pBase + 2 >= pos.length) {
            return;
        }

        float x = pos[pBase];
        float y = pos[pBase + 1];
        float z = pos[pBase + 2];

        float nx = 0f;
        float ny = 1f;
        float nz = 0f;

        if (normals != null && pBase + 2 < normals.length) {
            nx = normals[pBase];
            ny = normals[pBase + 1];
            nz = normals[pBase + 2];
        }

        float u = 0f;
        float v = 0f;

        if (uvs != null) {
            int uvBase = vi * 2;

            if (uvBase >= 0 && uvBase + 1 < uvs.length) {
                u = uvs[uvBase];
                v = uvs[uvBase + 1];
            }
        }

        vc.addVertex(pose.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(
                        UMaterialRenderUtil.resolveU(pass, binding, u),
                        UMaterialRenderUtil.resolveV(pass, binding, v)
                )
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}