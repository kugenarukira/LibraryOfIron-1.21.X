package net.ironedge.libraryofiron.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record DataKey<T, T1>(String id, Class<T> type) {

    public DataKey(String id, Class<T> type) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataKey<?, ?> other)) return false;
        return id.equals(other.id) && type.equals(other.type);
    }

    @Override
    public @NotNull String toString() {
        return "DataKey{id='" + id + "', type=" + type + "}";
    }
}