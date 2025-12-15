package net.ironedge.libraryofiron.capability;

import net.ironedge.libraryofiron.core.LoIContext;

public interface LoICapability<T> {

    /** Called when attached to an entity */
    void onAttach(LoIContext context, Object holder);

    /** Called on each tick (optional) */
    default void tick(LoIContext context, Object holder) {}

    /** Called when capability is removed or entity unloaded */
    default void onDetach(LoIContext context, Object holder) {}

    /** Returns the internal data type of the capability */
    T getData();
}