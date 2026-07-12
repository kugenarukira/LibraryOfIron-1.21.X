package net.ironedge.libraryofiron.render.umr.geo;

import com.google.gson.*;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Reader;
import java.util.*;

import static net.ironedge.libraryofiron.render.umr.geo.GeoModel.*;

public final class GeoJsonLoader {

    private GeoJsonLoader() {}

    public static GeoModel load(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

        JsonArray geos = root.getAsJsonArray("minecraft:geometry");
        if (geos == null || geos.isEmpty()) {
            throw new IllegalArgumentException("Missing minecraft:geometry");
        }

        // pick first geometry entry (your examples only have one)
        JsonObject geo0 = geos.get(0).getAsJsonObject();

        JsonObject desc = geo0.getAsJsonObject("description");
        int texW = getInt(desc, "texture_width", 64);
        int texH = getInt(desc, "texture_height", 64);

        JsonArray bonesArr = geo0.getAsJsonArray("bones");
        List<GeoBone> bones = new ArrayList<>();

        if (bonesArr != null) {
            for (JsonElement be : bonesArr) {
                JsonObject b = be.getAsJsonObject();
                String name = getString(b, "name", "unnamed");
                String parent = b.has("parent") ? b.get("parent").getAsString() : null;

                Vector3f pivot = getVec3(b.getAsJsonArray("pivot"), new Vector3f());
                Vector3f rot = getVec3(b.getAsJsonArray("rotation"), new Vector3f());

                List<GeoCube> cubes = new ArrayList<>();
                JsonArray cubesArr = b.getAsJsonArray("cubes");
                if (cubesArr != null) {
                    for (JsonElement ce : cubesArr) {
                        cubes.add(parseCube(ce.getAsJsonObject()));
                    }
                }

                bones.add(new GeoBone(name, parent, pivot, rot, cubes));
            }
        }

        return new GeoModel(texW, texH, bones);
    }

    private static GeoCube parseCube(JsonObject c) {
        Vector3f origin = getVec3(c.getAsJsonArray("origin"), new Vector3f());
        Vector3f size = getVec3(c.getAsJsonArray("size"), new Vector3f(1,1,1));

        Vector3f pivot = c.has("pivot") ? getVec3(c.getAsJsonArray("pivot"), null) : null;
        Vector3f rot = c.has("rotation") ? getVec3(c.getAsJsonArray("rotation"), new Vector3f()) : new Vector3f();

        EnumMap<Face, FaceUV> faces = new EnumMap<>(Face.class);

        // Bedrock geometry can have either:
        // - "uv": [x,y] (box-uv) OR
        // - "uv": { "north": {...}, ... } (per-face). Your files use per-face.
        JsonElement uvEl = c.get("uv");
        if (uvEl != null && uvEl.isJsonObject()) {
            JsonObject uvObj = uvEl.getAsJsonObject();
            parseFaceUV(uvObj, "north", Face.NORTH, faces);
            parseFaceUV(uvObj, "south", Face.SOUTH, faces);
            parseFaceUV(uvObj, "east",  Face.EAST,  faces);
            parseFaceUV(uvObj, "west",  Face.WEST,  faces);
            parseFaceUV(uvObj, "up",    Face.UP,    faces);
            parseFaceUV(uvObj, "down",  Face.DOWN,  faces);
        } else {
            // optional: support box-uv later
        }

        return new GeoCube(origin, size, pivot, rot, faces);
    }

    private static void parseFaceUV(JsonObject uvObj, String key, Face face, EnumMap<Face, FaceUV> out) {
        if (!uvObj.has(key)) return;
        JsonObject f = uvObj.getAsJsonObject(key);
        Vector2i uv = getVec2i(f.getAsJsonArray("uv"), new Vector2i());
        Vector2i uvSize = getVec2i(f.getAsJsonArray("uv_size"), new Vector2i());
        out.put(face, new FaceUV(uv, uvSize));
    }

    private static int getInt(JsonObject o, String k, int def) {
        return o != null && o.has(k) ? o.get(k).getAsInt() : def;
    }

    private static String getString(JsonObject o, String k, String def) {
        return o != null && o.has(k) ? o.get(k).getAsString() : def;
    }

    private static Vector3f getVec3(JsonArray a, Vector3f def) {
        if (a == null) return def;
        return new Vector3f(
                a.size() > 0 ? a.get(0).getAsFloat() : 0f,
                a.size() > 1 ? a.get(1).getAsFloat() : 0f,
                a.size() > 2 ? a.get(2).getAsFloat() : 0f
        );
    }

    private static Vector2i getVec2i(JsonArray a, Vector2i def) {
        if (a == null) return def;
        return new Vector2i(
                a.size() > 0 ? a.get(0).getAsInt() : 0,
                a.size() > 1 ? a.get(1).getAsInt() : 0
        );
    }
}