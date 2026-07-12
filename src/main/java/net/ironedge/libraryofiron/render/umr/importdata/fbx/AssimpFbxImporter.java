package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import net.ironedge.libraryofiron.render.umar.material.UMaterialImportBridge;
import net.ironedge.libraryofiron.render.umar.material.UMaterialInstance;
import net.ironedge.libraryofiron.render.umr.importdata.*;
import net.ironedge.libraryofiron.render.umr.mesh.MeshAsset;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVertexWeight;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIColor4D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public final class AssimpFbxImporter {

    private AssimpFbxImporter() {}

    public static MeshAsset importMeshAsset(
            String assetId,
            Path path
    ) {
        return importMeshAsset(
                assetId,
                path,
                UMRFbxImportOptions.defaults()
        );
    }

    private static void printMeshSummary(
            String name,
            float[] positions,
            float[] normals,
            float[] uvs,
            int[] indices,
            UMRSkinWeightsDef skinWeights
    ) {
        System.out.println("[UMR FBX] Surface '" + name + "'"
                + " verts=" + (positions != null ? positions.length / 3 : 0)
                + " indices=" + (indices != null ? indices.length : 0)
                + " tris=" + (indices != null ? indices.length / 3 : 0)
                + " normals=" + (normals != null)
                + " uvs=" + (uvs != null)
                + " skinWeights=" + (skinWeights != null));

        printBounds(name, positions);

        if (indices != null && indices.length >= 3) {
            System.out.println("[UMR FBX] First tri: "
                    + indices[0] + ", " + indices[1] + ", " + indices[2]);
        }
    }

    private static void printBounds(String name, float[] positions) {
        if (positions == null || positions.length == 0) {
            System.out.println("[UMR FBX] Surface '" + name + "' has no positions");
            return;
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;

        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < positions.length; i += 3) {
            float x = positions[i];
            float y = positions[i + 1];
            float z = positions[i + 2];

            if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
                System.out.println("[UMR FBX] Surface '" + name + "' has non-finite position at vertex " + (i / 3));
                return;
            }

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        System.out.println("[UMR FBX] Surface '" + name + "' bounds min=("
                + minX + ", " + minY + ", " + minZ + ") max=("
                + maxX + ", " + maxY + ", " + maxZ + ")");
    }

    public static MeshAsset importMeshAsset(
            String assetId,
            Path path,
            UMRFbxImportOptions options
    ) {
        UMRMeshAssetDef def = importDef(assetId, path, options);
        return UMRMeshAssetFactory.build(def);
    }

    public static UMRMeshAssetDef importDef(
            String assetId,
            Path path,
            UMRFbxImportOptions options
    ) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("assetId must not be blank");
        }

        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        if (options == null) {
            options = UMRFbxImportOptions.defaults();
        }

        AIScene scene = aiImportFile(
                path.toAbsolutePath().toString(),
                options.assimpFlags()
        );

        if (scene == null) {
            throw new RuntimeException("Assimp failed to import FBX: " + aiGetErrorString());
        }

        try {
            return readScene(assetId, scene, options);
        } finally {
            aiReleaseImport(scene);
        }
    }

    private static UMRMeshAssetDef readScene(
            String assetId,
            AIScene scene,
            UMRFbxImportOptions options
    ) {
        PointerBuffer meshes = scene.mMeshes();

        if (meshes == null || scene.mNumMeshes() <= 0) {
            throw new RuntimeException("FBX contains no meshes");
        }

        if (options.debugPrint) {
            System.out.println("[UMR FBX] Scene meshes=" + scene.mNumMeshes()
                    + " materials=" + scene.mNumMaterials()
                    + " embeddedTextures=" + scene.mNumTextures()
                    + " animations=" + scene.mNumAnimations());
        }

        BoneCollector boneCollector = new BoneCollector();

        Map<String, UMRImportedMaterialDef> materials = readMaterials(assetId, scene, options);

        List<UMRMeshSurfaceDef> surfaces = new ArrayList<>();

        for (int mi = 0; mi < scene.mNumMeshes(); mi++) {
            AIMesh mesh = AIMesh.create(meshes.get(mi));

            UMRMeshSurfaceDef surface = readMeshSurface(
                    mesh,
                    mi,
                    boneCollector,
                    options
            );

            surfaces.add(surface);
        }

        UMRSkeletonDef skeleton = buildSkeletonFromScene(scene, boneCollector);

        return new UMRMeshAssetDef(
                assetId,
                surfaces,
                Map.of(),
                skeleton,
                materials
        );
    }

    private static Map<String, UMRImportedMaterialDef> readMaterials(
            String assetId,
            AIScene scene,
            UMRFbxImportOptions options
    ) {
        Map<String, UMRImportedMaterialDef> out = new HashMap<>();

        PointerBuffer materials = scene.mMaterials();

        if (materials == null || scene.mNumMaterials() <= 0) {
            return out;
        }

        for (int mi = 0; mi < scene.mNumMaterials(); mi++) {
            AIMaterial material = AIMaterial.create(materials.get(mi));

            String slot = "material_" + mi;
            String name = slot;

            ResourceLocation baseTexture = readBaseTexture(
                    assetId,
                    slot,
                    material,
                    scene,
                    options
            );

            MaterialColor materialColor = readMaterialColor(material, slot, options);

            if (baseTexture == null) {
                baseTexture = FbxTextureResolver.resolveFallbackWhiteTexture(options);
            }

            UMRImportedMaterialDef def = new UMRImportedMaterialDef(
                    slot,
                    name,
                    baseTexture,
                    materialColor.argb,
                    materialColor.alpha
            );

            out.put(slot, def);

            if (options.debugPrint) {
                System.out.println("[UMR FBX] Material " + slot
                        + " baseTexture=" + baseTexture);
            }
        }

        return out;
    }

    private static MaterialColor readMaterialColor(
            AIMaterial material,
            String slot,
            UMRFbxImportOptions options
    ) {
        AIColor4D color = AIColor4D.calloc();

        try {
            if (readColorKey(material, "$clr.base", color)) {
                MaterialColor out = materialColor(color);

                if (options.debugPrint) {
                    System.out.println("[UMR FBX] Material " + slot
                            + " colorKey=BASE"
                            + " color=#" + Integer.toHexString(out.argb).toUpperCase()
                            + " alpha=" + out.alpha);
                }

                return out;
            }

            if (readColorKey(material, "$clr.diffuse", color)) {
                MaterialColor out = materialColor(color);

                if (options.debugPrint) {
                    System.out.println("[UMR FBX] Material " + slot
                            + " colorKey=DIFFUSE"
                            + " color=#" + Integer.toHexString(out.argb).toUpperCase()
                            + " alpha=" + out.alpha);
                }

                return out;
            }

            if (options.debugPrint) {
                System.out.println("[UMR FBX] Material " + slot
                        + " has no readable base/diffuse color; using white");
            }

            return new MaterialColor(0xFFFFFFFF, 1.0f);
        } finally {
            color.free();
        }
    }

    private static boolean readColorKey(
            AIMaterial material,
            String key,
            AIColor4D out
    ) {
        int result = aiGetMaterialColor(
                material,
                key,
                0,
                0,
                out
        );

        return result == aiReturn_SUCCESS;
    }

    private static MaterialColor materialColor(AIColor4D color) {
        int r = clamp255(color.r() * 255.0f);
        int g = clamp255(color.g() * 255.0f);
        int b = clamp255(color.b() * 255.0f);
        int a = clamp255(color.a() * 255.0f);

        // Some importers report alpha as 0 even when the material is visually opaque.
        if (a <= 0) {
            a = 255;
        }

        int argb = (a << 24) | (r << 16) | (g << 8) | b;
        return new MaterialColor(argb, a / 255.0f);
    }

    private static int clamp255(float value) {
        if (value < 0.0f) return 0;
        if (value > 255.0f) return 255;
        return Math.round(value);
    }

    private static final class MaterialColor {
        final int argb;
        final float alpha;

        MaterialColor(int argb, float alpha) {
            this.argb = argb;
            this.alpha = alpha;
        }
    }

    private static ResourceLocation readBaseTexture(
            String assetId,
            String slot,
            AIMaterial material,
            AIScene scene,
            UMRFbxImportOptions options
    ) {
        ResourceLocation diffuse = readFirstTextureOfType(
                assetId,
                slot,
                material,
                scene,
                options,
                aiTextureType_DIFFUSE,
                "DIFFUSE"
        );

        if (diffuse != null) {
            return diffuse;
        }

        ResourceLocation baseColor = readFirstTextureOfType(
                assetId,
                slot,
                material,
                scene,
                options,
                aiTextureType_BASE_COLOR,
                "BASE_COLOR"
        );

        if (baseColor != null) {
            return baseColor;
        }

        ResourceLocation unknown = readFirstTextureOfType(
                assetId,
                slot,
                material,
                scene,
                options,
                aiTextureType_UNKNOWN,
                "UNKNOWN"
        );

        return unknown;
    }

    private static ResourceLocation readFirstTextureOfType(
            String assetId,
            String slot,
            AIMaterial material,
            AIScene scene,
            UMRFbxImportOptions options,
            int textureType,
            String debugName
    ) {
        int reportedCount = aiGetMaterialTextureCount(material, textureType);

        // FBX/Assimp can report 0 here while index 0 still works.
        // So we always probe at least a few slots.
        int probeCount = Math.max(reportedCount, 8);

        if (options.debugPrint) {
            System.out.println("[UMR FBX] Material " + slot
                    + " textureType=" + debugName
                    + " reportedCount=" + reportedCount
                    + " probeCount=" + probeCount);
        }

        for (int textureIndex = 0; textureIndex < probeCount; textureIndex++) {
            ResourceLocation texture = readTextureOfTypeIndex(
                    assetId,
                    slot,
                    material,
                    scene,
                    options,
                    textureType,
                    debugName,
                    textureIndex
            );

            if (texture != null) {
                return texture;
            }
        }

        return null;
    }

    private static ResourceLocation readTextureOfTypeIndex(
            String assetId,
            String slot,
            AIMaterial material,
            AIScene scene,
            UMRFbxImportOptions options,
            int textureType,
            String debugName,
            int textureIndex
    ) {
        AIString path = AIString.calloc();

        try {
            int result = aiGetMaterialTexture(
                    material,
                    textureType,
                    textureIndex,
                    path,
                    (IntBuffer) null,
                    (IntBuffer) null,
                    (FloatBuffer) null,
                    (IntBuffer) null,
                    (IntBuffer) null,
                    (IntBuffer) null
            );

            if (result != aiReturn_SUCCESS) {
                if (options.debugPrint && textureIndex == 0) {
                    System.out.println("[UMR FBX] Material " + slot
                            + " textureType=" + debugName
                            + "[" + textureIndex + "]"
                            + " no texture result=" + result);
                }

                return null;
            }

            String raw = path.dataString();

            if (raw == null || raw.isBlank()) {
                return null;
            }

            if (options.debugPrint) {
                System.out.println("[UMR FBX] Material " + slot
                        + " textureType=" + debugName
                        + "[" + textureIndex + "]"
                        + " rawPath='" + raw + "'");
            }

            return FbxTextureResolver.resolveTexture(
                    assetId,
                    slot,
                    raw,
                    scene,
                    options
            );
        } finally {
            path.free();
        }
    }

    private static ResourceLocation textureResource(
            String rawPath,
            UMRFbxImportOptions options
    ) {
        String clean = rawPath
                .replace('\\', '/');

        int slash = clean.lastIndexOf('/');
        if (slash >= 0) {
            clean = clean.substring(slash + 1);
        }

        if (options.stripTextureExtensionToPng) {
            int dot = clean.lastIndexOf('.');
            if (dot >= 0) {
                clean = clean.substring(0, dot);
            }

            clean = clean + ".png";
        }

        clean = clean.toLowerCase()
                .replaceAll("[^a-z0-9_./-]", "_");

        String base = options.textureBasePath;

        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        return ResourceLocation.fromNamespaceAndPath(
                options.textureNamespace,
                base + "/" + clean
        );
    }
    private static UMRMeshSurfaceDef readMeshSurface(
            AIMesh mesh,
            int meshIndex,
            BoneCollector boneCollector,
            UMRFbxImportOptions options
    ) {
        String name = mesh.mName().dataString();

        if (name == null || name.isBlank()) {
            name = "surface_" + meshIndex;
        } else {
            name = name + "_" + meshIndex;
        }

        int vertexCount = mesh.mNumVertices();

        float[] positions = readPositions(mesh, vertexCount, options);
        float[] normals = readNormals(mesh, vertexCount, options);
        float[] uvs = readUvs(mesh, vertexCount, options.flipV);
        int[] indices = readIndices(mesh);

        UMRSkinWeightsDef skinWeights = readSkinWeights(
                mesh,
                vertexCount,
                boneCollector,
                options
        );

        if (options.debugPrint) {
            printMeshSummary(name, positions, normals, uvs, indices, skinWeights);
        }

        if (options.debugPrint) {
            System.out.println("[UMR FBX] Surface '" + name
                    + "' materialSlot=material_" + mesh.mMaterialIndex());
        }

        return new UMRMeshSurfaceDef(
                name,
                positions,
                normals,
                uvs,
                indices,
                "material_" + mesh.mMaterialIndex(),
                skinWeights
        );
    }

    private static float[] readPositions(
            AIMesh mesh,
            int vertexCount,
            UMRFbxImportOptions options
    ) {
        AIVector3D.Buffer source = mesh.mVertices();

        if (source == null) {
            throw new RuntimeException("Mesh has no vertex positions: " + mesh.mName().dataString());
        }

        float[] out = new float[vertexCount * 3];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D v = source.get(i);

            float x = v.x() * options.globalScale;
            float y = v.y() * options.globalScale;
            float z = v.z() * options.globalScale;

            if (options.convertBlenderToMinecraftAxes) {
                float oldY = y;
                y = z;
                z = -oldY;
            }

            int p = i * 3;
            out[p] = x;
            out[p + 1] = y;
            out[p + 2] = z;
        }

        return out;
    }

    private static float[] readUvs(
            AIMesh mesh,
            int vertexCount,
            boolean flipV
    ) {
        AIVector3D.Buffer source = mesh.mTextureCoords(0);

        if (source == null) {
            return null;
        }

        float[] out = new float[vertexCount * 2];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D uv = source.get(i);

            int p = i * 2;
            out[p] = uv.x();
            out[p + 1] = flipV ? 1.0f - uv.y() : uv.y();
        }

        return out;
    }

    private static float[] readNormals(
            AIMesh mesh,
            int vertexCount,
            UMRFbxImportOptions options
    ) {
        AIVector3D.Buffer source = mesh.mNormals();

        if (source == null) {
            return null;
        }

        float[] out = new float[vertexCount * 3];

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D n = source.get(i);

            float x = n.x();
            float y = n.y();
            float z = n.z();

            if (options.convertBlenderToMinecraftAxes) {
                float oldY = y;
                y = z;
                z = -oldY;
            }

            int p = i * 3;
            out[p] = x;
            out[p + 1] = y;
            out[p + 2] = z;
        }

        return out;
    }

    private static int[] readIndices(AIMesh mesh) {
        AIFace.Buffer faces = mesh.mFaces();

        if (faces == null) {
            return new int[0];
        }

        List<Integer> out = new ArrayList<>();

        for (int fi = 0; fi < mesh.mNumFaces(); fi++) {
            AIFace face = faces.get(fi);
            IntBuffer idx = face.mIndices();

            if (idx == null) {
                continue;
            }

            if (face.mNumIndices() != 3) {
                continue;
            }

            out.add(idx.get(0));
            out.add(idx.get(1));
            out.add(idx.get(2));
        }

        int[] indices = new int[out.size()];

        for (int i = 0; i < out.size(); i++) {
            indices[i] = out.get(i);
        }

        return indices;
    }

    private static UMRSkinWeightsDef readSkinWeights(
            AIMesh mesh,
            int vertexCount,
            BoneCollector boneCollector,
            UMRFbxImportOptions options
    ) {
        if (mesh.mNumBones() <= 0 || mesh.mBones() == null) {
            return null;
        }

        System.out.println("[UMR FBX] Mesh '" + mesh.mName().dataString()
                + "' bones=" + mesh.mNumBones()
                + " vertices=" + vertexCount);

        List<List<UMRVertexWeightDef>> byVertex = new ArrayList<>();

        for (int i = 0; i < vertexCount; i++) {
            byVertex.add(new ArrayList<>());
        }

        PointerBuffer bones = mesh.mBones();

        for (int bi = 0; bi < mesh.mNumBones(); bi++) {
            AIBone bone = AIBone.create(bones.get(bi));

            String boneName = bone.mName().dataString();

            System.out.println("[UMR FBX]   Bone influence '" + boneName
                    + "' weights=" + bone.mNumWeights());

            Matrix4f inverseBindRaw = toJoml(bone.mOffsetMatrix());
            Matrix4f bind = new Matrix4f(inverseBindRaw).invert();

            // Vertices are scaled by options.globalScale, so bind translations must match.
            scaleMatrixTranslation(bind, options.globalScale);

            // Recompute inverse bind from the scaled bind pose.
            Matrix4f inverseBind = new Matrix4f(bind).invert();

            int runtimeBoneIndex = boneCollector.registerBone(
                    boneName,
                    bind,
                    inverseBind
            );

            AIVertexWeight.Buffer weights = bone.mWeights();

            if (weights == null) {
                continue;
            }

            for (int wi = 0; wi < bone.mNumWeights(); wi++) {
                AIVertexWeight weight = weights.get(wi);

                int vertexId = weight.mVertexId();
                float value = weight.mWeight();

                if (vertexId < 0 || vertexId >= vertexCount) {
                    continue;
                }

                if (value <= 0f) {
                    continue;
                }

                byVertex.get(vertexId).add(new UMRVertexWeightDef(
                        runtimeBoneIndex,
                        value
                ));
            }
        }

        List<UMRVertexSkinDataDef> vertices = new ArrayList<>();

        for (List<UMRVertexWeightDef> weights : byVertex) {
            vertices.add(new UMRVertexSkinDataDef(normalizeAndLimit(weights, 4)));
        }

        int weightedVerts = 0;
        int maxWeightsOnVertex = 0;

        for (UMRVertexSkinDataDef v : vertices) {
            if (v != null && v.weightCount() > 0) {
                weightedVerts++;
                maxWeightsOnVertex = Math.max(maxWeightsOnVertex, v.weightCount());
            }
        }

        System.out.println("[UMR FBX] Skin weights summary weightedVerts="
                + weightedVerts + "/" + vertexCount
                + " maxWeightsOnVertex=" + maxWeightsOnVertex);

        return new UMRSkinWeightsDef(vertices);
    }

    private static List<UMRVertexWeightDef> normalizeAndLimit(
            List<UMRVertexWeightDef> weights,
            int maxWeights
    ) {
        if (weights == null || weights.isEmpty()) {
            return List.of();
        }

        weights = new ArrayList<>(weights);

        weights.sort((a, b) -> Float.compare(b.weight(), a.weight()));

        if (weights.size() > maxWeights) {
            weights = new ArrayList<>(weights.subList(0, maxWeights));
        }

        float total = 0f;

        for (UMRVertexWeightDef w : weights) {
            total += w.weight();
        }

        if (total <= 0f) {
            return List.of();
        }

        List<UMRVertexWeightDef> normalized = new ArrayList<>();

        for (UMRVertexWeightDef w : weights) {
            normalized.add(new UMRVertexWeightDef(
                    w.boneIndex(),
                    w.weight() / total
            ));
        }

        return normalized;
    }

    private static UMRSkeletonDef buildSkeletonFromScene(
            AIScene scene,
            BoneCollector boneCollector
    ) {
        if (boneCollector.isEmpty()) {
            return null;
        }

        AINode root = scene.mRootNode();

        if (root != null) {
            assignParentsFromNodeTree(root, -1, boneCollector);
        }

        UMRSkeletonDef skeleton = boneCollector.buildSkeletonDef();

        if (skeleton != null) {
            System.out.println("[UMR FBX] Skeleton bones=" + skeleton.boneCount());

            int shown = 0;
            for (UMRBoneDef bone : skeleton.bones()) {
                if (shown++ >= 12) break;

                System.out.println("[UMR FBX] Bone "
                        + bone.index()
                        + " name='" + bone.name()
                        + "' parent=" + bone.parentIndex());
            }
        }

        return skeleton;
    }

    private static void assignParentsFromNodeTree(
            AINode node,
            int nearestBoneParent,
            BoneCollector boneCollector
    ) {
        String nodeName = node.mName().dataString();

        int thisBone = boneCollector.indexOf(nodeName);
        int effectiveParent = nearestBoneParent;

        if (thisBone >= 0) {
            boneCollector.parentIndex(thisBone, nearestBoneParent);
            effectiveParent = thisBone;
        }

        PointerBuffer children = node.mChildren();

        if (children == null) {
            return;
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode child = AINode.create(children.get(i));
            assignParentsFromNodeTree(child, effectiveParent, boneCollector);
        }
    }

    /**
     * Assimp's AIMatrix4x4 exposes rows as:
     *
     * a1 a2 a3 a4
     * b1 b2 b3 b4
     * c1 c2 c3 c4
     * d1 d2 d3 d4
     *
     * JOML's Matrix4f constructor takes matrix components by m00, m01, etc.
     *
     * If imported skeletons appear transposed/twisted, this is the first method
     * we will tune.
     */
    private static Matrix4f toJoml(AIMatrix4x4 m) {
        return new Matrix4f(
                m.a1(), m.a2(), m.a3(), m.a4(),
                m.b1(), m.b2(), m.b3(), m.b4(),
                m.c1(), m.c2(), m.c3(), m.c4(),
                m.d1(), m.d2(), m.d3(), m.d4()
        );
    }

    private static float[] matrixArray(Matrix4f matrix) {
        float[] out = new float[16];
        matrix.get(out);
        return out;
    }

    private static void scaleMatrixTranslation(Matrix4f matrix, float scale) {
        matrix.m30(matrix.m30() * scale);
        matrix.m31(matrix.m31() * scale);
        matrix.m32(matrix.m32() * scale);
    }

    private static final class BoneCollector {

        private final List<String> names = new ArrayList<>();
        private final List<Integer> parents = new ArrayList<>();
        private final List<Matrix4f> bindPoses = new ArrayList<>();
        private final List<Matrix4f> inverseBindPoses = new ArrayList<>();
        private final Map<String, Integer> byName = new HashMap<>();

        int registerBone(
                String name,
                Matrix4f bindPose,
                Matrix4f inverseBindPose
        ) {
            if (name == null || name.isBlank()) {
                name = "bone_" + names.size();
            }

            Integer existing = byName.get(name);

            if (existing != null) {
                return existing;
            }

            int index = names.size();

            names.add(name);
            parents.add(-1);
            bindPoses.add(new Matrix4f(bindPose));
            inverseBindPoses.add(new Matrix4f(inverseBindPose));
            byName.put(name, index);

            return index;
        }

        int indexOf(String name) {
            if (name == null) {
                return -1;
            }

            return byName.getOrDefault(name, -1);
        }

        void parentIndex(int boneIndex, int parentIndex) {
            if (boneIndex < 0 || boneIndex >= parents.size()) {
                return;
            }

            parents.set(boneIndex, parentIndex);
        }

        boolean isEmpty() {
            return names.isEmpty();
        }

        UMRSkeletonDef buildSkeletonDef() {
            List<UMRBoneDef> bones = new ArrayList<>();

            for (int i = 0; i < names.size(); i++) {
                bones.add(new UMRBoneDef(
                        names.get(i),
                        i,
                        parents.get(i),
                        matrixArray(bindPoses.get(i)),
                        matrixArray(inverseBindPoses.get(i))
                ));
            }

            return new UMRSkeletonDef(bones);
        }
    }
    public static UMRFbxImportResult importMeshAssetWithMaterials(
            String assetId,
            Path path,
            UMRFbxImportOptions options
    ) {
        UMRMeshAssetDef def = importDef(assetId, path, options);
        MeshAsset asset = UMRMeshAssetFactory.build(def);

        Map<String, UMRImportedMaterialDef> texturedMaterials = new HashMap<>();

        for (var entry : def.materials().entrySet()) {
            UMRImportedMaterialDef material = entry.getValue();

            if (material != null && material.hasBaseTexture()) {
                texturedMaterials.put(entry.getKey(), material);
            } else if (options.debugPrint) {
                System.out.println("[UMR FBX] Material " + entry.getKey()
                        + " has no texture; renderer will use fallback material");
            }
        }

        Map<String, UMaterialInstance> materialInstances =
                UMaterialImportBridge.buildImportedMaterialInstances(
                        options.textureNamespace,
                        assetId,
                        texturedMaterials
                );

        return new UMRFbxImportResult(
                asset,
                def.materials(),
                materialInstances
        );
    }
}