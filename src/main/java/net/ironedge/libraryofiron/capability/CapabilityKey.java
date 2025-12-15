package net.ironedge.libraryofiron.capability;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public record CapabilityKey<T>(String id, Class<T> type) {

    public CapabilityKey(String id, Class<T> type) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CapabilityKey<?> other)) return false;
        return id.equals(other.id) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public @NotNull String toString() {
        return "CapabilityKey{" + "id='" + id + '\'' + ", type=" + type + '}';
    }
}
