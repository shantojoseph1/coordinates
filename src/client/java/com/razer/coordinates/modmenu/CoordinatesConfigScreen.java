package com.razer.coordinates.modmenu;

import com.razer.coordinates.config.ConfigManager;
import com.razer.coordinates.modmenu.widget.DropdownWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class CoordinatesConfigScreen extends Screen {
    private final Screen parent;
    private ConfigManager.CoordinatesConfig config;
    
    // UI Elements
    private CheckboxWidget enabledCheckbox;
    private CheckboxWidget backgroundCheckbox;
    private CheckboxWidget biomeCheckbox;
    private CheckboxWidget directionCheckbox;
    private CheckboxWidget showXCheckbox;
    private CheckboxWidget showYCheckbox;
    private CheckboxWidget showZCheckbox;
    
    private TextFieldWidget xOffsetField;
    private TextFieldWidget yOffsetField;
    private TextFieldWidget colorHexField;
    private DropdownWidget positionDropdown;
    
    private SliderWidget scaleSlider;
    private ButtonWidget resetButton;
    private ButtonWidget saveButton;
    private ButtonWidget saveCloseButton;
    
    // Position presets
    private static final List<DropdownWidget.DropdownOption> POSITION_PRESETS = Arrays.asList(
        new DropdownWidget.DropdownOption("Above Hotbar", 0, -50),
        new DropdownWidget.DropdownOption("Top Center", 0, 10),
        new DropdownWidget.DropdownOption("Top Left", -200, 10),
        new DropdownWidget.DropdownOption("Top Right", 200, 10),
        new DropdownWidget.DropdownOption("Bottom Left", -200, -30),
        new DropdownWidget.DropdownOption("Bottom Right", 200, -30),
        new DropdownWidget.DropdownOption("Custom", 0, 0)
    );
    
    public CoordinatesConfigScreen(Screen parent) {
        super(Text.literal("Coordinates by Razer Settings"));
        this.parent = parent;
        this.config = ConfigManager.getConfig();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int leftColumn = centerX - 150;
        int rightColumn = centerX + 10;
        int startY = 40;
        int spacing = 25;
        int currentY = startY;
        
        // Title spacing
        currentY += 10;
        
        // Enable/Disable - Full width at top
        enabledCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Enable Coordinates Display"), this.textRenderer)
            .pos(centerX - 100, currentY)
            .checked(config.enabled)
            .callback((checkbox, checked) -> {
                config.enabled = checked;
                updateChildrenState();
            })
            .build());
        currentY += spacing + 10;
        
        // === LEFT COLUMN ===
        int leftY = currentY;
        
        // Display Options Section
        leftY += 5;
        
        // Show X, Y, Z checkboxes in left column
        showXCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show X"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showX)
            .callback((checkbox, checked) -> config.showX = checked)
            .build());
        leftY += spacing;
        
        showYCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show Y"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showY)
            .callback((checkbox, checked) -> config.showY = checked)
            .build());
        leftY += spacing;
        
        showZCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show Z"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showZ)
            .callback((checkbox, checked) -> config.showZ = checked)
            .build());
        leftY += spacing;
        
        // Additional info checkboxes
        biomeCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show Biome"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showBiome)
            .callback((checkbox, checked) -> config.showBiome = checked)
            .build());
        leftY += spacing;
        
        directionCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show Direction"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showDirection)
            .callback((checkbox, checked) -> config.showDirection = checked)
            .build());
        leftY += spacing;
        
        // Background
        backgroundCheckbox = addDrawableChild(CheckboxWidget.builder(Text.literal("Show Background"), this.textRenderer)
            .pos(leftColumn, leftY)
            .checked(config.showBackground)
            .callback((checkbox, checked) -> config.showBackground = checked)
            .build());
        
        // === RIGHT COLUMN ===
        int rightY = currentY + 5;
        
        // Position dropdown - make sure it's rendered on top
        DropdownWidget.DropdownOption currentPreset = findCurrentPreset();
        positionDropdown = new DropdownWidget(rightColumn, rightY, 140, 20, 
            POSITION_PRESETS, currentPreset, this::onPositionPresetSelected);
        addDrawableChild(positionDropdown);
        rightY += spacing + 25; // Extra space for dropdown expansion
        
        // Custom position fields
        addDrawableChild(ButtonWidget.builder(Text.literal("X:"), 
            button -> {}).dimensions(rightColumn, rightY, 25, 20).build()).active = false;
        
        xOffsetField = addDrawableChild(new TextFieldWidget(this.textRenderer, rightColumn + 30, rightY, 50, 20, Text.literal("X")));
        xOffsetField.setText(String.valueOf(config.xOffset));
        xOffsetField.setChangedListener(text -> {
            try {
                config.xOffset = Integer.parseInt(text);
                updateDropdownSelection();
            } catch (NumberFormatException ignored) {}
        });
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Y:"), 
            button -> {}).dimensions(rightColumn + 85, rightY, 25, 20).build()).active = false;
        
        yOffsetField = addDrawableChild(new TextFieldWidget(this.textRenderer, rightColumn + 115, rightY, 50, 20, Text.literal("Y")));
        yOffsetField.setText(String.valueOf(config.yOffset));
        yOffsetField.setChangedListener(text -> {
            try {
                config.yOffset = Integer.parseInt(text);
                updateDropdownSelection();
            } catch (NumberFormatException ignored) {}
        });
        rightY += spacing + 10;
        
        // Color hex input with preview
        // Color section header
        rightY += 5;
        
        // Color hex input - properly aligned
        colorHexField = addDrawableChild(new TextFieldWidget(this.textRenderer, rightColumn, rightY, 100, 20, Text.literal("Hex Color")));
        colorHexField.setText(String.format("%06X", config.textColor & 0xFFFFFF));
        colorHexField.setPlaceholder(Text.literal("FFFFFF"));
        colorHexField.setChangedListener(text -> {
            try {
                // Remove # if present
                String cleanHex = text.startsWith("#") ? text.substring(1) : text;
                if (cleanHex.length() == 6) {
                    int color = Integer.parseInt(cleanHex, 16);
                    config.textColor = 0xFF000000 | color;
                }
            } catch (NumberFormatException ignored) {}
        });
        rightY += spacing + 10;
        
        // Scale slider
        scaleSlider = addDrawableChild(new SliderWidget(rightColumn, rightY, 140, 20, 
            Text.literal("Scale: " + String.format("%.1f", config.scale)), 
            (config.scale - 0.1f) / 2.9f) {
            
            @Override
            protected void updateMessage() {
                config.scale = 0.1f + (float) (this.value * 2.9f);
                this.setMessage(Text.literal("Scale: " + String.format("%.1f", config.scale)));
            }
            
            @Override
            protected void applyValue() {
                config.scale = 0.1f + (float) (this.value * 2.9f);
            }
        });
        
        // Action buttons at bottom - ensure they're visible and below all dropdowns
        int buttonY = Math.max(leftY + spacing + 20, rightY + 50);
        
        resetButton = addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), 
            button -> {
                resetToDefaults();
                updateFields();
            }).dimensions(centerX - 120, buttonY, 70, 20).build());
        
        saveButton = addDrawableChild(ButtonWidget.builder(Text.literal("Save"), 
            button -> {
                ConfigManager.saveConfig();
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("§6Configuration saved!"), false);
                }
            }).dimensions(centerX - 45, buttonY, 70, 20).build());
        
        saveCloseButton = addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), 
            button -> {
                ConfigManager.saveConfig();
                this.close();
            }).dimensions(centerX + 30, buttonY, 90, 20).build());
        
        updateChildrenState();
    }
    
    private DropdownWidget.DropdownOption findCurrentPreset() {
        for (DropdownWidget.DropdownOption preset : POSITION_PRESETS) {
            if (preset.xOffset == config.xOffset && preset.yOffset == config.yOffset) {
                return preset;
            }
        }
        return POSITION_PRESETS.get(POSITION_PRESETS.size() - 1); // Custom
    }
    
    private void onPositionPresetSelected(DropdownWidget.DropdownOption option) {
        if (!option.label.equals("Custom")) {
            config.xOffset = option.xOffset;
            config.yOffset = option.yOffset;
            updateFields();
        }
    }
    
    private void updateDropdownSelection() {
        DropdownWidget.DropdownOption currentPreset = findCurrentPreset();
        positionDropdown.setSelectedOption(currentPreset);
    }
    
    private void updateChildrenState() {
        boolean enabled = enabledCheckbox.isChecked();
        
        showXCheckbox.active = enabled;
        showYCheckbox.active = enabled;
        showZCheckbox.active = enabled;
        biomeCheckbox.active = enabled;
        directionCheckbox.active = enabled;
        backgroundCheckbox.active = enabled;
        xOffsetField.active = enabled;
        yOffsetField.active = enabled;
        colorHexField.active = enabled;
        scaleSlider.active = enabled;
        positionDropdown.active = enabled;
        resetButton.active = enabled;
        saveButton.active = enabled;
        saveCloseButton.active = enabled;
    }
    
    private void updateFields() {
        xOffsetField.setText(String.valueOf(config.xOffset));
        yOffsetField.setText(String.valueOf(config.yOffset));
        colorHexField.setText(String.format("%06X", config.textColor & 0xFFFFFF));
        
        // Update scale slider - recreate it since setValue is private
        if (scaleSlider != null) {
            // Remove old slider
            this.remove(scaleSlider);
            
            // Create new slider with correct value
            int rightColumn = this.width / 2 + 10;
            int sliderY = scaleSlider.getY(); // Keep same Y position
            
            scaleSlider = addDrawableChild(new SliderWidget(rightColumn, sliderY, 140, 20, 
                Text.literal("Scale: " + String.format("%.1f", config.scale)), 
                (config.scale - 0.1f) / 2.9f) {
                
                @Override
                protected void updateMessage() {
                    config.scale = 0.1f + (float) (this.value * 2.9f);
                    this.setMessage(Text.literal("Scale: " + String.format("%.1f", config.scale)));
                }
                
                @Override
                protected void applyValue() {
                    config.scale = 0.1f + (float) (this.value * 2.9f);
                }
            });
        }
        
        updateDropdownSelection();
    }
    
    private void resetToDefaults() {
        ConfigManager.CoordinatesConfig defaults = new ConfigManager.CoordinatesConfig();
        
        config.enabled = defaults.enabled;
        config.xOffset = defaults.xOffset;
        config.yOffset = defaults.yOffset;
        config.textColor = defaults.textColor;
        config.backgroundColor = defaults.backgroundColor;
        config.showBackground = defaults.showBackground;
        config.showX = defaults.showX;
        config.showY = defaults.showY;
        config.showZ = defaults.showZ;
        config.showBiome = defaults.showBiome;
        config.showDirection = defaults.showDirection;
        config.scale = defaults.scale;
        
        // Recreate the screen to update all widgets with reset values
        this.clearChildren();
        this.init();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Render all children except dropdowns first
        for (var child : this.children()) {
            if (!(child instanceof DropdownWidget)) {
                if (child instanceof net.minecraft.client.gui.Drawable drawable) {
                    drawable.render(context, mouseX, mouseY, delta);
                }
            }
        }
        
        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        
        // Draw section headers
        context.drawTextWithShadow(this.textRenderer, Text.literal("Display Options").formatted(Formatting.YELLOW), 
            this.width / 2 - 150, 75, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Position & Appearance").formatted(Formatting.YELLOW), 
            this.width / 2 + 10, 75, 0xFFFFFF);
        
        // Draw field labels
        if (colorHexField != null) {
            context.drawTextWithShadow(this.textRenderer, "Color (Hex):", 
                this.width / 2 + 10, colorHexField.getY() - 15, 0xFFFFFF);
            
            // Draw color preview box next to the hex field
            int previewX = colorHexField.getX() + colorHexField.getWidth() + 10;
            int previewY = colorHexField.getY();
            int previewSize = 20;
            
            // Draw preview background (white border)
            context.fill(previewX - 1, previewY - 1, previewX + previewSize + 1, previewY + previewSize + 1, 0xFFFFFFFF);
            
            // Draw the actual color preview
            int previewColor = config.textColor | 0xFF000000; // Ensure full opacity for preview
            context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, previewColor);
            
            // Draw "Preview" label below the box
            context.drawTextWithShadow(this.textRenderer, "Preview", 
                previewX - 5, previewY + previewSize + 5, 0xAAAAAA);
        }
        
        // Render dropdowns last so they appear on top
        for (var child : this.children()) {
            if (child instanceof DropdownWidget dropdown) {
                dropdown.render(context, mouseX, mouseY, delta);
            }
        }
        
        // Draw current coordinates preview at the very bottom with proper spacing
        // Draw keybind info at very bottom
        String keybindText = "Keybinds: 'O' = Config Screen, 'H' = Toggle Display";
        int keybindX = this.width / 2 - this.textRenderer.getWidth(keybindText) / 2;
        context.drawTextWithShadow(this.textRenderer, Text.literal(keybindText).formatted(Formatting.GRAY), 
            keybindX, this.height - 40, 0xAAAAAA);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle dropdown clicks first to prevent overlap issues
        if (positionDropdown != null && positionDropdown.isDropdownOpen()) {
            boolean handled = positionDropdown.mouseClicked(mouseX, mouseY, button);
            if (handled || !positionDropdown.isMouseOver(mouseX, mouseY)) {
                return handled;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void close() {
        ConfigManager.saveConfig();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}