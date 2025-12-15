package net.ironedge.libraryofiron.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ReloadableDataSet {

    private final List<Runnable> reloadListeners = new CopyOnWriteArrayList<>();

    public void addReloadListener(Runnable listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        reloadListeners.add(listener);
    }

    public void reload() {
        // In practice, reparse JSON from disk
        reloadListeners.forEach(Runnable::run);
    }
}
