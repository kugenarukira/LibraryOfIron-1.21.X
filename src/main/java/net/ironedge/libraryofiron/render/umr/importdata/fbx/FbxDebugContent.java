package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import net.ironedge.libraryofiron.render.core.RenderEngine;
import net.ironedge.libraryofiron.render.umar.material.UMaterialDebugInstances;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAssetRegistry;
import net.ironedge.libraryofiron.render.umr.mesh.MeshInstance;
import net.ironedge.libraryofiron.render.umr.mesh.render.MeshRenderNode;
import org.joml.Vector3f;

import java.nio.file.Path;

public final class FbxDebugContent {

    private static boolean installed = false;

    public static MeshInstance instance;

    private FbxDebugContent() {}

    public static void install(RenderEngine engine) {
        if (installed) return;
        installed = true;

        Path path = Path.of("umr_import/Untitled.fbx");

        if (!java.nio.file.Files.exists(path)) {
            System.out.println("[UMR FBX] No test FBX found at: " + path.toAbsolutePath());
            return;
        }

        UMRFbxImportOptions options = UMRFbxImportOptions.defaults();
        options.debugPrint = true;
        options.globalScale = 0.01f;
        options.flipV = false;
        options.convertBlenderToMinecraftAxes = false;

        UMRFbxImportResult result;

        try {
            result = AssimpFbxImporter.importMeshAssetWithMaterials(
                    "debug_fbx_import",
                    path,
                    options
            );
        } catch (Throwable t) {
            System.err.println("[UMR FBX] Failed to import test FBX: " + path.toAbsolutePath());
            t.printStackTrace();
            return;
        }

        MeshAsset asset = result.asset();
        MeshAssetRegistry.register(asset);

        instance = new MeshInstance(
                "debug_fbx_import_instance",
                asset
        );

        instance.translation(new Vector3f(6f, 62f, 0f));
        instance.scale(new Vector3f(1f, 1f, 1f));

        if (asset.hasSkeleton()) {
            instance.skeletonPose(FbxBindPoseFactory.createBindPose(asset.skeleton()));
            System.out.println("[UMR FBX] Assigned imported bind pose to instance");
        }

        engine.graph().addNode(new MeshRenderNode(
                instance,
                UMaterialDebugInstances.FLAT_WHITE_INSTANCE,
                result.materialInstances()
        ));

        if (asset.hasSkeleton()) {
            int debugBone = chooseDebugBone(asset);

            if (debugBone >= 0) {
                engine.graph().addNode(new FbxSkeletonPoseDebugNode(
                        instance,
                        debugBone,
                        45f
                ));

                System.out.println("[UMR FBX] Added skeleton pose debug node for bone "
                        + debugBone
                        + " name='" + asset.skeleton().bone(debugBone).name() + "'");
            } else {
                System.out.println("[UMR FBX] No valid child bone found for pose debug");
            }
        }

        if (asset.hasSkeleton()) {
            System.out.println("[UMR FBX] Runtime skeleton boneCount=" + asset.skeleton().boneCount());

            for (int i = 0; i < asset.skeleton().boneCount(); i++) {
                var bone = asset.skeleton().bone(i);

                System.out.println("[UMR FBX] Runtime bone "
                        + i
                        + " name='" + bone.name()
                        + "' parent=" + bone.parentIndex());
            }
        }

        System.out.println("[UMR FBX] Imported " + asset.id()
                + " surfaces=" + asset.surfacesView().size()
                + " materials=" + result.materialInstances().size()
                + " hasSkeleton=" + asset.hasSkeleton());
        printRuntimeSkinSummary(asset);
    }

    private static void printRuntimeSkinSummary(MeshAsset asset) {
        if (asset == null) return;

        for (var surface : asset.surfacesView()) {
            var weights = surface.skinWeights();

            if (weights == null) {
                System.out.println("[UMR FBX] Runtime surface '" + surface.name()
                        + "' has no skin weights");
                continue;
            }

            int vertexCount = surface.vertexCount();
            int weightedVerts = 0;
            int maxWeights = 0;

            for (int i = 0; i < vertexCount; i++) {
                var skin = weights.vertex(i);

                if (skin == null || skin.weights() == null || skin.weights().length == 0) {
                    continue;
                }

                weightedVerts++;
                maxWeights = Math.max(maxWeights, skin.weights().length);
            }

            System.out.println("[UMR FBX] Runtime surface '" + surface.name()
                    + "' weightedVerts=" + weightedVerts + "/" + vertexCount
                    + " maxWeights=" + maxWeights);
        }
    }

    private static int chooseDebugBone(MeshAsset asset) {
        if (asset == null || !asset.hasSkeleton()) {
            return -1;
        }

        var skeleton = asset.skeleton();

        // Prefer a non-root bone, because bending root often just swings the whole model.
        for (int i = 0; i < skeleton.boneCount(); i++) {
            var bone = skeleton.bone(i);

            if (bone.parentIndex() >= 0) {
                return i;
            }
        }

        // Fallback: if all bones report no parent, use bone 1 if available.
        if (skeleton.boneCount() > 1) {
            return 1;
        }

        return -1;
    }
}