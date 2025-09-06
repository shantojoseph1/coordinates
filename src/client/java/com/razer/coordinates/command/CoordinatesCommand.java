package com.razer.coordinates.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.razer.coordinates.config.ConfigManager;
import com.razer.coordinates.modmenu.CoordinatesConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CoordinatesCommand {
    
    public static void register() {
        System.out.println("[Coordinates by Razer] Registering client commands...");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            System.out.println("[Coordinates by Razer] Command registration callback triggered");
            dispatcher.register(ClientCommandManager.literal("coordinates")
                .executes(CoordinatesCommand::showStatus)
                .then(ClientCommandManager.literal("toggle")
                    .executes(CoordinatesCommand::toggle))
                .then(ClientCommandManager.literal("position")
                    .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
                            .executes(CoordinatesCommand::setPosition))))
                .then(ClientCommandManager.literal("preset")
                    .then(ClientCommandManager.literal("hotbar")
                        .executes(CoordinatesCommand::presetHotbar))
                    .then(ClientCommandManager.literal("top")
                        .executes(CoordinatesCommand::presetTop)))
                .then(ClientCommandManager.literal("color")
                    .then(ClientCommandManager.argument("hex", StringArgumentType.string())
                        .executes(CoordinatesCommand::setColor)))
                .then(ClientCommandManager.literal("background")
                    .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(CoordinatesCommand::setBackground)))
                .then(ClientCommandManager.literal("scale")
                    .then(ClientCommandManager.argument("scale", FloatArgumentType.floatArg(0.1f, 3.0f))
                        .executes(CoordinatesCommand::setScale)))
                .then(ClientCommandManager.literal("biome")
                    .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(CoordinatesCommand::setBiome)))
                .then(ClientCommandManager.literal("direction")
                    .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(CoordinatesCommand::setDirection)))
                .then(ClientCommandManager.literal("reset")
                    .executes(CoordinatesCommand::reset))
                .then(ClientCommandManager.literal("menu")
                    .executes(CoordinatesCommand::openMenu)));
            
            // Shortcut command
            dispatcher.register(ClientCommandManager.literal("coords")
                .executes(CoordinatesCommand::toggle));
        });
    }
    
    private static int showStatus(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        
        context.getSource().sendFeedback(Text.literal("§6=== Coordinates Display Status ==="));
        context.getSource().sendFeedback(Text.literal("§7Enabled: " + (config.enabled ? "§aYes" : "§cNo")));
        context.getSource().sendFeedback(Text.literal("§7Position: §f" + config.xOffset + ", " + config.yOffset));
        context.getSource().sendFeedback(Text.literal("§7Scale: §f" + String.format("%.1f", config.scale) + "x"));
        context.getSource().sendFeedback(Text.literal("§7Background: " + (config.showBackground ? "§aEnabled" : "§cDisabled")));
        context.getSource().sendFeedback(Text.literal("§7Biome: " + (config.showBiome ? "§aEnabled" : "§cDisabled")));
        context.getSource().sendFeedback(Text.literal("§7Direction: " + (config.showDirection ? "§aEnabled" : "§cDisabled")));
        context.getSource().sendFeedback(Text.literal("§7Keybinds: §eO §7= Config, §eH §7= Toggle"));
        context.getSource().sendFeedback(Text.literal("§7Available presets: hotbar, top"));
        context.getSource().sendFeedback(Text.literal("§7Use §e/coordinates scale <0.1-3.0> §7to change text size"));
        
        return 1;
    }
    
    private static int toggle(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        config.enabled = !config.enabled;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Coordinates display " + 
            (config.enabled ? "§aenabled" : "§cdisabled")));
        
        return 1;
    }
    
    private static int setPosition(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        int x = IntegerArgumentType.getInteger(context, "x");
        int y = IntegerArgumentType.getInteger(context, "y");
        
        config.xOffset = x;
        config.yOffset = y;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Position set to: §f" + x + ", " + y));
        
        return 1;
    }
    
    private static int presetHotbar(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        config.xOffset = 0;
        config.yOffset = ConfigManager.CoordinatesConfig.ABOVE_HOTBAR;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Position set to: §aAbove Hotbar"));
        
        return 1;
    }
    
    private static int presetTop(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        config.xOffset = 0;
        config.yOffset = 10;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Position set to: §aTop Center"));
        
        return 1;
    }
    
    private static int setColor(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        String hexColor = StringArgumentType.getString(context, "hex");
        
        try {
            // Remove # if present
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            
            // Parse hex color
            int color = Integer.parseInt(hexColor, 16);
            
            // Add alpha channel if not present
            if (hexColor.length() == 6) {
                color = 0xFF000000 | color;
            }
            
            config.textColor = color;
            ConfigManager.saveConfig();
            
            context.getSource().sendFeedback(Text.literal("§6Text color set to: §f#" + hexColor.toUpperCase()));
            
        } catch (NumberFormatException e) {
            context.getSource().sendError(Text.literal("§cInvalid hex color format. Use format: FFFFFF or #FFFFFF"));
            return 0;
        }
        
        return 1;
    }
    
    private static int setBackground(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        config.showBackground = enabled;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Background " + 
            (enabled ? "§aenabled" : "§cdisabled")));
        
        return 1;
    }
    
    private static int setScale(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        float scale = FloatArgumentType.getFloat(context, "scale");
        
        config.scale = scale;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Scale set to: §f" + scale + "x"));
        
        return 1;
    }
    
    private static int setBiome(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        config.showBiome = enabled;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Biome display " + 
            (enabled ? "§aenabled" : "§cdisabled")));
        
        return 1;
    }
    
    private static int setDirection(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        config.showDirection = enabled;
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Direction display " + 
            (enabled ? "§aenabled" : "§cdisabled")));
        
        return 1;
    }
    
    private static int reset(CommandContext<FabricClientCommandSource> context) {
        ConfigManager.CoordinatesConfig newConfig = new ConfigManager.CoordinatesConfig();
        
        // Copy the new config values
        ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
        config.enabled = newConfig.enabled;
        config.xOffset = newConfig.xOffset;
        config.yOffset = newConfig.yOffset;
        config.textColor = newConfig.textColor;
        config.backgroundColor = newConfig.backgroundColor;
        config.showBackground = newConfig.showBackground;
        config.showX = newConfig.showX;
        config.showY = newConfig.showY;
        config.showZ = newConfig.showZ;
        config.showBiome = newConfig.showBiome;
        config.showDirection = newConfig.showDirection;
        config.scale = newConfig.scale;
        
        ConfigManager.saveConfig();
        
        context.getSource().sendFeedback(Text.literal("§6Configuration reset to defaults"));
        
        return 1;
    }
    
    private static int openMenu(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.execute(() -> {
                client.setScreen(new CoordinatesConfigScreen(null));
            });
            context.getSource().sendFeedback(Text.literal("§6Opening configuration menu..."));
        }
        
        return 1;
    }
}