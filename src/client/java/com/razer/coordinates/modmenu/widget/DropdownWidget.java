package com.razer.coordinates.modmenu.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public class DropdownWidget extends ClickableWidget {
    private final List<DropdownOption> options;
    private final Consumer<DropdownOption> onSelectionChanged;
    private DropdownOption selectedOption;
    private boolean isOpen = false;
    private final int dropdownHeight;
    
    public static class DropdownOption {
        public final String label;
        public final int xOffset;
        public final int yOffset;
        
        public DropdownOption(String label, int xOffset, int yOffset) {
            this.label = label;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
    
    public DropdownWidget(int x, int y, int width, int height, List<DropdownOption> options, 
                         DropdownOption initialSelection, Consumer<DropdownOption> onSelectionChanged) {
        super(x, y, width, height, Text.literal("Position Preset"));
        this.options = options;
        this.selectedOption = initialSelection;
        this.onSelectionChanged = onSelectionChanged;
        this.dropdownHeight = Math.min(options.size() * 20, 120); // Max 6 items visible
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Draw main button with better contrast
        int buttonColor = this.isHovered() ? 0xFF444444 : 0xFF333333;
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, buttonColor);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, 0xFFAAAAAA);
        
        // Draw selected option text
        String displayText = selectedOption != null ? selectedOption.label : "Select Position";
        int textX = this.getX() + 8;
        int textY = this.getY() + (this.height - client.textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(client.textRenderer, displayText, textX, textY, 0xFFFFFFFF);
        
        // Draw dropdown arrow
        String arrow = isOpen ? "▲" : "▼";
        int arrowX = this.getX() + this.width - 16;
        context.drawTextWithShadow(client.textRenderer, arrow, arrowX, textY, 0xFFFFFFFF);
        
        // Draw dropdown options if open
        if (isOpen) {
            int dropdownY = this.getY() + this.height;
            
            // Background for dropdown with better contrast
            context.fill(this.getX(), dropdownY, this.getX() + this.width, dropdownY + dropdownHeight, 0xFF222222);
            context.drawBorder(this.getX(), dropdownY, this.width, dropdownHeight, 0xFFAAAAAA);
            
            // Draw options
            for (int i = 0; i < options.size(); i++) {
                DropdownOption option = options.get(i);
                int optionY = dropdownY + (i * 20);
                
                // Skip if option would be outside visible area
                if (optionY + 20 > dropdownY + dropdownHeight) {
                    break;
                }
                
                // Highlight hovered option
                if (mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                    mouseY >= optionY && mouseY < optionY + 20) {
                    context.fill(this.getX(), optionY, this.getX() + this.width, optionY + 20, 0xFF555555);
                }
                
                // Highlight selected option
                if (option == selectedOption) {
                    context.fill(this.getX(), optionY, this.getX() + this.width, optionY + 20, 0xFF4A90E2);
                }
                
                // Draw option text with proper alpha
                int optionTextX = this.getX() + 8;
                int optionTextY = optionY + (20 - client.textRenderer.fontHeight) / 2;
                context.drawTextWithShadow(client.textRenderer, option.label, optionTextX, optionTextY, 0xFFFFFFFF);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isValidClickButton(button)) {
            return false;
        }
        
        // Check if clicking on main button
        if (mouseX >= this.getX() && mouseX < this.getX() + this.width &&
            mouseY >= this.getY() && mouseY < this.getY() + this.height) {
            
            if (isOpen) {
                // Close dropdown if clicking on main button while open
                isOpen = false;
                return true;
            } else {
                // Open dropdown
                isOpen = true;
                return true;
            }
        }
        
        // Check if clicking on dropdown options
        if (isOpen) {
            int dropdownY = this.getY() + this.height;
            if (mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                mouseY >= dropdownY && mouseY < dropdownY + dropdownHeight) {
                
                int optionIndex = (int) ((mouseY - dropdownY) / 20);
                if (optionIndex >= 0 && optionIndex < options.size()) {
                    selectedOption = options.get(optionIndex);
                    onSelectionChanged.accept(selectedOption);
                    isOpen = false;
                    return true;
                }
            } else {
                // Close dropdown if clicking outside
                isOpen = false;
                return false;
            }
        }
        
        return false;
    }
    
    public void setSelectedOption(DropdownOption option) {
        this.selectedOption = option;
    }
    
    public DropdownOption getSelectedOption() {
        return selectedOption;
    }
    
    public boolean isDropdownOpen() {
        return isOpen;
    }
    
    public void closeDropdown() {
        isOpen = false;
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(net.minecraft.client.gui.screen.narration.NarrationPart.TITLE, 
            Text.literal("Position preset dropdown, current selection: " + 
                (selectedOption != null ? selectedOption.label : "None")));
    }
}