package net.ironedge.libraryofiron.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Objects;

public interface LoICodec<T> {

    T decode(JsonElement json);

    JsonElement encode(T object);

    static <T> LoICodec<T> fromGson(Class<T> clazz, Gson gson) {
        Objects.requireNonNull(clazz, "Class type cannot be null");
        Objects.requireNonNull(gson, "Gson cannot be null");

        return new LoICodec<>() {
            @Override
            public T decode(JsonElement json) {
                if (json == null) {
                    throw new IllegalArgumentException("JsonElement cannot be null");
                }
                return gson.fromJson(json, clazz);
            }

            @Override
            public JsonElement encode(T object) {
                if (object == null) {
                    throw new IllegalArgumentException("Object to encode cannot be null");
                }
                return gson.toJsonTree(object, clazz);
            }
        };
    }
}
