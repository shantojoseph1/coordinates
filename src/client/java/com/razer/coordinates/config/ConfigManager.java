package com.razer.coordinates.config;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "coordinates-by-razer.json");
    private static CoordinatesConfig config;
    
    public static void initialize() {
        System.out.println("[Coordinates by Razer] Initializing configuration system...");
        loadConfig();
        System.out.println("[Coordinates by Razer] Configuration system ready");
    }
    
    public static CoordinatesConfig getConfig() {
        if (config == null) {
            config = new CoordinatesConfig();
        }
        return config;
    }
    
    public static void saveConfig() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
            System.out.println("[Coordinates by Razer] Configuration saved successfully");
        } catch (IOException e) {
            System.err.println("[Coordinates by Razer] Failed to save configuration: " + e.getMessage());
        }
    }
    
    private static void loadConfig() {
        try {
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    config = GSON.fromJson(reader, CoordinatesConfig.class);
                    System.out.println("[Coordinates by Razer] Configuration loaded successfully");
                }
            } else {
                config = new CoordinatesConfig();
                saveConfig();
                System.out.println("[Coordinates by Razer] Created default configuration");
            }
        } catch (Exception e) {
            System.err.println("[Coordinates by Razer] Failed to load configuration, using defaults: " + e.getMessage());
            config = new CoordinatesConfig();
        }
    }
    
    public static class CoordinatesConfig {
        public boolean enabled = true;
        public int xOffset = 0;
        public int yOffset = -50; // Above hotbar by default
        public int textColor = 0xFFFFFFFF; // White with alpha
        public int separatorColor = 0xAAAAAA; // Light gray for separators
        public int backgroundColor = 0x80000000; // Semi-transparent black
        public boolean showBackground = false;
        public boolean showX = true;
        public boolean showY = true;
        public boolean showZ = true;
        public boolean showBiome = false;
        public boolean showDirection = false;
        public String format = "X: %d Y: %d Z: %d";
        public float scale = 1.0f;
        
        // Preset positions
        public static final int ABOVE_HOTBAR = -50;
        public static final int BELOW_HOTBAR = 20;
        public static final int TOP_LEFT = -200;
        public static final int TOP_RIGHT = -200;
    }
}