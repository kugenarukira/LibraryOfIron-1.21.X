package net.ironedge.libraryofiron.render.umr.mesh.deform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ironedge.libraryofiron.render.core.FrameContext;
import net.ironedge.libraryofiron.render.core.RenderNode;
import net.ironedge.libraryofiron.render.core.RenderPhase;
import net.ironedge.libraryofiron.render.umar.material.*;
import net.ironedge.libraryofiron.render.umar.state.UMaterialStateContext;
import net.ironedge.libraryofiron.render.umar.texture.UTextureBinding;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.MeshSurface;
import net.ironedge.libraryofiron.render.umr.morph.MorphBlender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;

public final class DeformedMeshRenderNode extends RenderNode {

    private final MeshInstance instance;
    private final UMaterialInstance material;
    private final UMaterialResolver resolver = new UMaterialResolver();
    private final SurfaceMeshBinding directBinding;
    private final WeightedSurfaceMeshBinding weightedBinding;

    public DeformedMeshRenderNode(
            MeshInstance instance,
            UMaterialInstance material,
            SurfaceMeshBinding binding
    ) {
        super(RenderPhase.DEBUG);
        this.instance = Objects.requireNonNull(instance, "instance");
        this.material = Objects.requireNonNull(material, "material");
        this.directBinding = Objects.requireNonNull(binding, "binding");
        this.weightedBinding = null;
    }

    public DeformedMeshRenderNode(
            MeshInstance instance,
            UMaterialInstance material,
            WeightedSurfaceMeshBinding binding
    ) {
        super(RenderPhase.DEBUG);
        this.instance = Objects.requireNonNull(instance, "instance");
        this.material = Objects.requireNonNull(material, "material");
        this.directBinding = null;
        this.weightedBinding = Objects.requireNonNull(binding, "binding");
    }

    @Override
    public void render(FrameContext frame) {
        PoseStack ps = frame.attachment("poseStack", PoseStack.class);
        if (ps == null) return;

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        UMaterialStateContext stateContext = buildStateContext(frame);
        UMaterialPassPlan passPlan = resolver.resolve(material, stateContext);
        if (passPlan.isEmpty()) return;

        Map<String, float[]> blendedPositions = MorphBlender.blendPositions(
                instance.asset(),
                instance.morphState()
        );

        var cam = frame.cameraPos();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        PoseStack.Pose pose = ps.last();
        int overlay = OverlayTexture.NO_OVERLAY;

        for (UMaterialRenderData pass : passPlan.passes()) {
            ResourceLocation texture = resolvePrimaryTexture(pass);
            if (texture == null) continue;

            RenderType rt = RenderType.entityCutoutNoCull(texture);
            if (rt == null) continue;

            VertexConsumer vc = buffers.getBuffer(rt);

            int argb = pass.color();
            int a = UMaterialRenderUtil.colorA(argb, pass.alpha());
            int r = UMaterialRenderUtil.colorR(argb);
            int g = UMaterialRenderUtil.colorG(argb);
            int b = UMaterialRenderUtil.colorB(argb);

            int light = UMaterialRenderTypes.isFullBright(pass)
                    ? LightTexture.FULL_BRIGHT
                    : LightTexture.FULL_BRIGHT; // replace with real packed light later

            var bindingTex = resolveBinding(pass);

            for (MeshSurface surface : instance.asset().surfacesView()) {
                float[] pos;
                float[] normals;
                float[] uvs = surface.uvsUnsafe();
                int[] indices = surface.indicesUnsafe();

                if (posOrIndicesMissing(surface, indices)) continue;

                if (isBoundSurface(surface)) {
                    pos = deformSurface(surface);
                    if (pos == null) continue;
                    normals = MeshNormalRecalculator.recomputeNormals(pos, indices);
                } else {
                    pos = blendedPositions.getOrDefault(surface.name(), surface.positionsUnsafe());
                    normals = surface.normalsUnsafe();
                }

                if (pos == null) continue;

                for (int i = 0; i < indices.length; i += 3) {
                    emitVertex(vc, pose, pos, normals, uvs, indices[i],     pass, bindingTex, r, g, b, a, overlay, light);
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 1], pass, bindingTex, r, g, b, a, overlay, light);
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 2], pass, bindingTex, r, g, b, a, overlay, light);

                    // duplicate final vertex so quad-mode render types receive 4 verts
                    emitVertex(vc, pose, pos, normals, uvs, indices[i + 2], pass, bindingTex, r, g, b, a, overlay, light);
                }
            }

            buffers.endBatch(rt);
        }

        ps.popPose();
    }

    private static void emitVertex(
            VertexConsumer vc,
            PoseStack.Pose pose,
            float[] pos,
            float[] normals,
            float[] uvs,
            int vi,
            UMaterialRenderData pass,
            net.ironedge.libraryofiron.render.umar.texture.UTextureBinding bindingTex,
            int r, int g, int b, int a,
            int overlay,
            int light
    ) {
        int pBase = vi * 3;

        float x = pos[pBase];
        float y = pos[pBase + 1];
        float z = pos[pBase + 2];

        float nx = 0f, ny = 1f, nz = 0f;
        if (normals != null) {
            nx = normals[pBase];
            ny = normals[pBase + 1];
            nz = normals[pBase + 2];
        }

        float u = 0f, v = 0f;
        if (uvs != null) {
            int uvBase = vi * 2;
            u = uvs[uvBase];
            v = uvs[uvBase + 1];
        }

        float finalU = UMaterialRenderUtil.resolveU(pass, bindingTex, u);
        float finalV = UMaterialRenderUtil.resolveV(pass, bindingTex, v);

        vc.addVertex(pose.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(finalU, finalV)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
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

    private boolean posOrIndicesMissing(MeshSurface surface, int[] indices) {
        return surface == null || surface.positionsUnsafe() == null || indices == null;
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

    private net.ironedge.libraryofiron.render.umar.texture.UTextureBinding resolveBinding(UMaterialRenderData pass) {
        var binding = pass.texture(UMaterialTextureSlot.BASE);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.EMISSIVE);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.OVERLAY);
        if (binding == null) binding = pass.texture(UMaterialTextureSlot.NOISE);
        return binding;
    }

    private boolean isBoundSurface(MeshSurface surface) {
        if (weightedBinding != null) {
            return surface.name().equals(weightedBinding.meshSurfaceName());
        }
        if (directBinding != null) {
            return surface.name().equals(directBinding.meshSurfaceName());
        }
        return false;
    }

    private float[] deformSurface(MeshSurface surface) {
        if (weightedBinding != null) {
            return WeightedSurfaceMeshDeformer.deformPositionsWorld(surface, weightedBinding);
        }
        if (directBinding != null) {
            return SurfaceMeshDeformer.deformPositionsWorld(surface, directBinding);
        }
        return null;
    }
}