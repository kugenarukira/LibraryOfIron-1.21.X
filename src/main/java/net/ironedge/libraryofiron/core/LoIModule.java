package net.ironedge.libraryofiron.core;

public class LoIModule {
    /** Called during mod construction */
    void onRegister(LoIContext context) {}

    /** Shared setup (registries, data, capabilities) */
    void onCommonSetup(LoIContext context) {}

    /** Client-only setup (renderers, visuals) */
    void onClientSetup(LoIContext context) {}

    /** Dedicated server setup */
    void onServerSetup(LoIContext context) {}

    /** For clean shutdown or hot-reload support later */
    void onShutdown(LoIContext context) {}
}