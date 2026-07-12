package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import static org.lwjgl.assimp.Assimp.*;

public final class UMRFbxImportOptions {

    public boolean triangulate = true;
    public boolean generateNormalsIfMissing = true;
    public boolean limitBoneWeights = true;
    public boolean joinIdenticalVertices = false;

    public boolean flipV = false;
    public float globalScale = 0.01f;

    public String textureNamespace = "libraryofiron";
    public String textureBasePath = "textures/umat/fbx";
    public boolean stripTextureExtensionToPng = true;
    public boolean loadEmbeddedTextures = true;

    /**
     * First FBX pass should be noisy so we can see what Assimp is actually giving us.
     */
    public boolean debugPrint = true;

    /**
     * Keep this false for the first test. We will turn it on only after we see axis issues.
     */
    public boolean convertBlenderToMinecraftAxes = false;

    public static UMRFbxImportOptions defaults() {
        return new UMRFbxImportOptions();
    }

    public int assimpFlags() {
        int flags = 0;

        if (triangulate) {
            flags |= aiProcess_Triangulate;
        }

        if (generateNormalsIfMissing) {
            flags |= aiProcess_GenSmoothNormals;
        }

        if (limitBoneWeights) {
            flags |= aiProcess_LimitBoneWeights;
        }

        if (joinIdenticalVertices) {
            flags |= aiProcess_JoinIdenticalVertices;
        }

        flags |= aiProcess_ImproveCacheLocality;
        flags |= aiProcess_SortByPType;
        flags |= aiProcess_ValidateDataStructure;
        flags |= aiProcess_FindInvalidData;

        return flags;
    }
}