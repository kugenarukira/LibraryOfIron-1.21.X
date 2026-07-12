package net.ironedge.libraryofiron.render.bridge;

public enum TipDistanceMode {
    STATIC,            // fixed distance (e.g. exact min or exact chosen)
    RANDOM_ONCE,       // choose random when activated (e.g. on sneak press)
    RANDOM_CONTINUOUS  // jitter continuously over time
}