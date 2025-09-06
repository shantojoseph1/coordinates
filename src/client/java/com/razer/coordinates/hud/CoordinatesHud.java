package com.razer.coordinates.hud;

import com.razer.coordinates.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CoordinatesHud {
    
    public static void register() {
        System.out.println("[Coordinates by Razer] Registering HUD render callback...");
        HudRenderCallback.EVENT.register(CoordinatesHud::onHudRender);
        System.out.println("[Coordinates by Razer] HUD render callback registered successfully");
    }
    
    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        try {
            ConfigManager.CoordinatesConfig config = ConfigManager.getConfig();
            
            if (!config.enabled) {
                return;
            }
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) {
                return;
            }
            
            // Get player position
            BlockPos pos = client.player.getBlockPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            
            // Build coordinate components
            StringBuilder leftText = new StringBuilder();
            StringBuilder centerText = new StringBuilder();
            StringBuilder rightText = new StringBuilder();
            
            // Center: XYZ coordinates
            if (config.showX) {
                centerText.append("X: ").append(x);
            }
            
            if (config.showY) {
                if (centerText.length() > 0) centerText.append(" ");
                centerText.append("Y: ").append(y);
            }
            
            if (config.showZ) {
                if (centerText.length() > 0) centerText.append(" ");
                centerText.append("Z: ").append(z);
            }
            
            // Left: Biome
            if (config.showBiome) {
                try {
                    var biomeEntry = client.world.getBiome(pos);
                    var biomeKey = biomeEntry.getKey();
                    
                    if (biomeKey.isPresent()) {
                        // Convert snake_case to readable format
                        String biomeName = biomeKey.get().getValue().getPath().replace("_", " ");
                        biomeName = biomeName.substring(0, 1).toUpperCase() + biomeName.substring(1);
                        leftText.append(biomeName);
                    } else {
                        leftText.append("Unknown");
                    }
                } catch (Exception e) {
                    leftText.append("Unknown");
                }
            }
            
            // Right: Direction
            if (config.showDirection) {
                Direction facing = client.player.getHorizontalFacing();
                rightText.append(facing.asString().toUpperCase());
            }
            
            // Calculate positions
            int screenWidth = drawContext.getScaledWindowWidth();
            int screenHeight = drawContext.getScaledWindowHeight();
            
            // Center coordinates
            String centerString = centerText.toString();
            if (!centerString.isEmpty()) {
                int centerWidth = client.textRenderer.getWidth(centerString);
                int scaledCenterWidth = (int) (centerWidth * config.scale);
                int centerX = (screenWidth - scaledCenterWidth) / 2 + config.xOffset;
                int centerY;
                
                if (config.yOffset < 0) {
                    centerY = screenHeight + config.yOffset - (int) (client.textRenderer.fontHeight * config.scale);
                } else {
                    centerY = config.yOffset;
                }
                
                // Calculate total width for background
                String leftString = leftText.toString();
                String rightString = rightText.toString();
                
                int totalWidth = scaledCenterWidth;
                int leftWidth = 0;
                int rightWidth = 0;
                
                if (!leftString.isEmpty()) {
                    leftWidth = (int) ((client.textRenderer.getWidth(leftString) + 20) * config.scale); // Include separator space
                    totalWidth += leftWidth;
                }
                
                if (!rightString.isEmpty()) {
                    rightWidth = (int) ((client.textRenderer.getWidth(rightString) + 20) * config.scale); // Include separator space
                    totalWidth += rightWidth;
                }
                
                // Draw background if enabled
                if (config.showBackground) {
                    int bgX = (leftWidth > 0) ? centerX - leftWidth - 4 : centerX - 4;
                    int bgY = centerY - 2;
                    int bgWidth = totalWidth + 8;
                    int bgHeight = (int) (client.textRenderer.fontHeight * config.scale) + 4;
                    
                    drawContext.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, config.backgroundColor);
                }
                
                // Draw left biome
                if (!leftString.isEmpty()) {
                    int leftX = centerX - (int) (client.textRenderer.getWidth(leftString) * config.scale) - (int) (20 * config.scale);
                    
                    // Use drawText with scale parameter for 1.21.6 compatibility
                    drawScaledText(drawContext, client, leftString, leftX, centerY, config.textColor, config.scale);
                    
                    // Draw separator
                    int sepX = centerX - (int) (10 * config.scale);
                    drawScaledText(drawContext, client, "|", sepX, centerY, config.separatorColor, config.scale);
                }
                
                // Draw center coordinates
                drawScaledText(drawContext, client, centerString, centerX, centerY, config.textColor, config.scale);
                
                // Draw right direction
                if (!rightString.isEmpty()) {
                    int rightX = centerX + scaledCenterWidth + (int) (20 * config.scale);
                    
                    drawScaledText(drawContext, client, rightString, rightX, centerY, config.textColor, config.scale);
                    
                    // Draw separator
                    int sepX = centerX + scaledCenterWidth + (int) (10 * config.scale);
                    drawScaledText(drawContext, client, "|", sepX, centerY, config.separatorColor, config.scale);
                }
            }
            
        } catch (Exception e) {
            // Silently handle rendering errors to prevent spam
        }
    }
    
    /**
     * Helper method to draw scaled text without using matrix operations
     */
    private static void drawScaledText(DrawContext drawContext, MinecraftClient client, String text, int x, int y, int color, float scale) {
        if (scale == 1.0f) {
            // No scaling needed, use regular method
            drawContext.drawTextWithShadow(client.textRenderer, text, x, y, color);
        } else {
            // For scaled text, we need to use a different approach
            // In 1.21.6, we can use the drawText method with transformation
            try {
                // Calculate scaled position
                float scaledX = x / scale;
                float scaledY = y / scale;
                
                // Apply scaling using the new matrix system
                var matrices = drawContext.getMatrices();
                matrices.scale(scale, scale);
                
                // Draw the text at scaled coordinates
                drawContext.drawTextWithShadow(client.textRenderer, text, (int) scaledX, (int) scaledY, color);
                
                // Reset scaling
                matrices.scale(1.0f / scale, 1.0f / scale);
                
            } catch (Exception e) {
                // Fallback to regular drawing if scaling fails
                drawContext.drawTextWithShadow(client.textRenderer, text, x, y, color);
            }
        }
    }
}