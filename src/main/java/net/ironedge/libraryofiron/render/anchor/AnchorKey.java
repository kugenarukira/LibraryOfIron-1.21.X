package net.ironedge.libraryofiron.render.anchor;

import java.util.Objects;

public final class AnchorKey {

    private final String id;

    public AnchorKey(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnchorKey)) return false;
        return id.equals(((AnchorKey) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AnchorKey[" + id + "]";
    }
}
