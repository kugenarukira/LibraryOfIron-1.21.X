package net.ironedge.libraryofiron.render.umr.importdata.fbx;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.assimp.AITexel;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class FbxTextureResolver {

    private FbxTextureResolver() {
    }
    private static final Map<ResourceLocation, NativeImage> PENDING_DYNAMIC_TEXTURES = new HashMap<>();
    private static final Set<ResourceLocation> REGISTERED_DYNAMIC_TEXTURES = new HashSet<>();

    public static ResourceLocation resolveTexture(
            String assetId,
            String slot,
            String rawPath,
            AIScene scene,
            UMRFbxImportOptions options
    ) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }

        rawPath = rawPath.replace('\\', '/');

        if (rawPath.startsWith("*")) {
            if (!options.loadEmbeddedTextures) {
                System.out.println("[UMR FBX] Embedded texture skipped because loadEmbeddedTextures=false: " + rawPath);
                return null;
            }

            return resolveEmbeddedTexture(assetId, slot, rawPath, scene, options);
        }

        return resolveExternalTexture(rawPath, options);
    }

    public static void flushPendingDynamicTextures() {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.getTextureManager() == null) {
            return;
        }

        if (PENDING_DYNAMIC_TEXTURES.isEmpty()) {
            return;
        }

        var iterator = PENDING_DYNAMIC_TEXTURES.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();

            ResourceLocation id = entry.getKey();
            NativeImage image = entry.getValue();

            if (!REGISTERED_DYNAMIC_TEXTURES.contains(id)) {
                mc.getTextureManager().register(
                        id,
                        new DynamicTexture(() -> id.toString(), image)
                );

                REGISTERED_DYNAMIC_TEXTURES.add(id);

                System.out.println("[UMR FBX] Flushed pending dynamic texture " + id);
            }

            iterator.remove();
        }
    }

    private static void registerOrQueueDynamicTexture(
            ResourceLocation id,
            NativeImage image,
            UMRFbxImportOptions options
    ) {
        if (id == null || image == null) {
            return;
        }

        if (REGISTERED_DYNAMIC_TEXTURES.contains(id)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.getTextureManager() == null) {
            PENDING_DYNAMIC_TEXTURES.put(id, image);

            if (options.debugPrint) {
                System.out.println("[UMR FBX] Queued dynamic texture until Minecraft is ready: " + id);
            }

            return;
        }

        mc.getTextureManager().register(
                id,
                new DynamicTexture(() -> id.toString(), image)
        );

        REGISTERED_DYNAMIC_TEXTURES.add(id);

        if (options.debugPrint) {
            System.out.println("[UMR FBX] Registered dynamic texture " + id);
        }
    }

    private static final Set<ResourceLocation> REGISTERED_WHITE_TEXTURES = new HashSet<>();

    public static ResourceLocation resolveFallbackWhiteTexture(UMRFbxImportOptions options) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                options.textureNamespace,
                "dynamic/fbx/_white"
        );

        if (!REGISTERED_DYNAMIC_TEXTURES.contains(id) && !PENDING_DYNAMIC_TEXTURES.containsKey(id)) {
            NativeImage image = new NativeImage(1, 1, false);
            image.setPixel(0, 0, 0xFFFFFFFF);

            registerOrQueueDynamicTexture(id, image, options);
        }

        return id;
    }

    private static ResourceLocation resolveExternalTexture(
            String rawPath,
            UMRFbxImportOptions options
    ) {
        String clean = rawPath;

        int slash = clean.lastIndexOf('/');
        if (slash >= 0) {
            clean = clean.substring(slash + 1);
        }

        int dot = clean.lastIndexOf('.');
        if (dot >= 0) {
            clean = clean.substring(0, dot);
        }

        clean = clean.toLowerCase()
                .replaceAll("[^a-z0-9_./-]", "_");

        return ResourceLocation.fromNamespaceAndPath(
                options.textureNamespace,
                options.textureBasePath + "/" + clean + ".png"
        );
    }

    private static ResourceLocation resolveEmbeddedTexture(
            String assetId,
            String slot,
            String rawPath,
            AIScene scene,
            UMRFbxImportOptions options
    ) {
        if (scene == null || scene.mTextures() == null || scene.mNumTextures() <= 0) {
            return null;
        }

        int index = parseEmbeddedIndex(rawPath);
        if (index < 0 || index >= scene.mNumTextures()) {
            return null;
        }
        if (options.debugPrint) {
            System.out.println("[UMR FBX] Resolving embedded texture "
                    + rawPath
                    + " index=" + index
                    + " sceneTextures=" + scene.mNumTextures());
        }

        AITexture texture = AITexture.create(scene.mTextures().get(index));

        NativeImage image;

        try {
            image = readEmbeddedTexture(texture);
        } catch (Exception e) {
            System.err.println("[UMR FBX] Failed to read embedded texture " + rawPath);
            e.printStackTrace();
            return null;
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                options.textureNamespace,
                "dynamic/fbx/"
                        + sanitize(assetId)
                        + "/"
                        + sanitize(slot)
                        + "/embedded_"
                        + index
        );

        registerOrQueueDynamicTexture(id, image, options);

        System.out.println("[UMR FBX] Prepared embedded texture " + rawPath + " as " + id);

        return id;
    }

    private static int parseEmbeddedIndex(String rawPath) {
        try {
            return Integer.parseInt(rawPath.substring(1));
        } catch (Exception ignored) {
            return -1;
        }
    }

    private static NativeImage readEmbeddedTexture(AITexture texture) throws IOException {
        if (texture.mHeight() == 0) {
            return readCompressedEmbeddedTexture(texture);
        }

        return readRawEmbeddedTexture(texture);
    }

    private static NativeImage readCompressedEmbeddedTexture(AITexture texture) throws IOException {
        int byteCount = texture.mWidth();

        AITexel.Buffer data = texture.pcData();
        ByteBuffer bytes = MemoryUtil.memByteBuffer(data.address(), byteCount);

        byte[] copy = new byte[byteCount];
        bytes.get(copy);

        return NativeImage.read(new ByteArrayInputStream(copy));
    }

    private static NativeImage readRawEmbeddedTexture(AITexture texture) {
        int width = texture.mWidth();
        int height = texture.mHeight();

        NativeImage image = new NativeImage(width, height, false);
        AITexel.Buffer data = texture.pcData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                AITexel texel = data.get(y * width + x);

                int r = texel.r() & 0xFF;
                int g = texel.g() & 0xFF;
                int b = texel.b() & 0xFF;
                int a = texel.a() & 0xFF;

                int argb = (a << 24) | (r << 16) | (g << 8) | b;

                image.setPixel(x, y, argb);
            }
        }

        return image;
    }

    private static String sanitize(String in) {
        if (in == null || in.isBlank()) {
            return "unnamed";
        }

        return in.toLowerCase()
                .replaceAll("[^a-z0-9_./-]", "_");
    }
}