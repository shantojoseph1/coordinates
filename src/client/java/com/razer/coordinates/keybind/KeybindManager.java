package com.razer.coordinates.keybind;

import com.razer.coordinates.config.ConfigManager;
import com.razer.coordinates.modmenu.CoordinatesConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    private static KeyBinding configKeybind;
    private static KeyBinding toggleKeybind;
    
    public static void register() {
        System.out.println("[Coordinates by Razer] Registering keybinds...");
        
        // Register config screen keybind
        configKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.coordinates.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.coordinates"
        ));
        
        // Register toggle keybind
        toggleKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.coordinates.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.coordinates"
        ));
        
        // Register tick event to handle keybind presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKeybind.wasPressed()) {
                openConfigScreen();
            }
            
            while (toggleKeybind.wasPressed()) {
                toggleCoordinates();
            }
        });
        
        System.out.println("[Coordinates by Razer] Keybinds registered successfully");
        System.out.println("[Coordinates by Razer] Press 'O' to open config screen");
        System.out.println("[Coordinates by Razer] Press 'H' to toggle coordinates");
    }
    
    private static void openConfigScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) {
            client.setScreen(new CoordinatesConfigScreen(null));
        }
    }
    
    private static void toggleCoordinates() {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        config.enabled = !config.enabled;
        ConfigManager.saveConfig();
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(net.minecraft.text.Text.literal("§6Coordinates display " + 
                (config.enabled ? "§aenabled" : "§cdisabled")), false);
        }
    }
}