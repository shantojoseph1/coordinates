package com.razer.coordinates;

import com.razer.coordinates.config.ConfigManager;
import com.razer.coordinates.hud.CoordinatesHud;
import com.razer.coordinates.command.CoordinatesCommand;
import com.razer.coordinates.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;

public class CoordinatesByRazerClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        try {
            System.out.println("=== [Coordinates by Razer] Starting initialization ===");
            System.out.println("[Coordinates by Razer] Mod version: 1.0.0");
            System.out.println("[Coordinates by Razer] Minecraft version: " + net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion());
            System.out.println("[Coordinates by Razer] Fabric Loader version: " + net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion());
            
            // Initialize configuration
            ConfigManager.initialize();
            System.out.println("[Coordinates by Razer] Configuration system initialized");
            
            // Register HUD
            CoordinatesHud.register();
            System.out.println("[Coordinates by Razer] HUD registered");
            
            // Register commands
            CoordinatesCommand.register();
            System.out.println("[Coordinates by Razer] Commands registered");
            
            // Register keybinds
            KeybindManager.register();
            System.out.println("[Coordinates by Razer] Keybinds registered");
            
            System.out.println("=== [Coordinates by Razer] Initialization completed successfully! ===");
            System.out.println("[Coordinates by Razer] Use /coords to toggle coordinates display");
            System.out.println("[Coordinates by Razer] Use /coordinates for configuration options");
            
        } catch (Exception e) {
            System.err.println("=== [Coordinates by Razer] INITIALIZATION FAILED! ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}