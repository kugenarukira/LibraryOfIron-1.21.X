package net.ironedge.libraryofiron.core;

import net.ironedge.libraryofiron.core.registry.LoIRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod(LoICore.MOD_ID)
public class LoICore {

    public static final String MOD_ID = "libraryofiron";

    private static LoICore instance;
    private static LoIContext context;

    public LoICore(IEventBus modEventBus) {
        instance = this;

        // Initialize context
        context = new LoIContext();

        LoILog.info("LoICore initialized");

        // Register modules
        registerModules();

        // Event listeners
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onServerSetup);
    }

    public static LoICore get() {
        return instance;
    }

    public static LoIContext context() {
        return context;
    }

    private void registerModules() {
        LoILog.info("Registering modules...");
        // Add modules here if needed
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        LoILog.info("Common setup");
        context.fireCommonSetup();

        // Safe place to register all capabilities
        LoIRegistry.registerAll();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LoILog.info("Client setup");
        context.fireClientSetup();
    }

    private void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LoILog.info("Server setup");
        context.fireServerSetup();
    }
}
