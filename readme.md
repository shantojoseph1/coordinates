# Coordinates by Razer

A lightweight client-side Minecraft Fabric mod that displays your current coordinates above the hotbar with extensive customization options.

## Features

-  **Real-time coordinate display** (X, Y, Z) with individual toggles
-  **Fully customizable appearance** - position, colors, scale, and background
-  **Additional information display** - biome and cardinal direction
-  **Multiple configuration methods** - in-game commands, keybinds, and GUI
-  **Responsive design** with preset positions and custom positioning
-  **Client-side only** - no server installation required
-  **Compatible** with Minecraft 1.21.6+ and Fabric Loader 0.17.2+
-  **ModMenu integration** for easy configuration access

## Installation

1. **Prerequisites:**
   - Minecraft 1.21.6, 1.21.7, or 1.21.8
 

2. **Installation Steps:**
   - Download the latest release of Coordinates by Razer
   - Place the mod file in your `.minecraft/mods` folder
   
3. **Dependencies:**
   - [ModMenu](https://modrinth.com/mod/modmenu) for in-game configuration GUI

## Quick Start

- **Toggle coordinates:** Press `H` or use `/coords`
- **Open config screen:** Press `O` or use `/coordinates menu`
- **View all settings:** Use `/coordinates` command


### Display Components
- **Coordinates:** Individual toggles for X, Y, and Z coordinates
- **Biome:** Shows current biome name (e.g., "Plains", "Dark Forest")
- **Direction:** Shows cardinal direction you're facing (N, S, E, W)
- **Background:** Optional semi-transparent background for better readability


## Commands Reference

### Quick Commands
```
/coords                           # Toggle coordinates on/off
/coordinates                      # Show current configuration
```

### Configuration Commands
```
/coordinates toggle               # Toggle display
/coordinates position <x> <y>     # Set custom position offset
/coordinates color <hex>          # Set text color (FFFFFF or #00FF00)
/coordinates background <true|false>  # Enable/disable background
/coordinates scale <0.1-3.0>      # Set display scale
/coordinates biome <true|false>   # Show/hide biome information
/coordinates direction <true|false>   # Show/hide facing direction
/coordinates reset                # Reset all settings to defaults
/coordinates menu                 # Open configuration GUI
```


## Keybinds

| Key | Action | Description |
|-----|--------|-------------|
| `H` | Toggle Display | Quickly show/hide coordinates |
| `O` | Open Config | Access the configuration screen |

*Keybinds can be changed in Minecraft's Controls settings under "Coordinates by Razer" category.*

## Technical Specifications

### Compatibility
- **Minecraft Versions:** 1.21.6, 1.21.7, 1.21.8
- **Fabric Loader:** 0.17.2 or higher
- **Fabric API:** 0.127.1+ for your Minecraft version
- **Java:** 21 or higher
- **Environment:** Client-side only


## Contributing

Contributions are welcome! Please feel free to:
- Report bugs and issues
- Suggest new features
- Submit pull requests
- Improve documentation

## Support

For support, updates, and community:
- **Website:** [razermc.online](https://razermc.online)
- **Discord:** [Join our community](https://discord.gg/k8dXxS5kE3)
- **Buy me a coffee:**


## License

This project is licensed under the MIT License - see the LICENSE file for details.
---

**Author:** not.razer  
**Website:** [razermc.online](https://razermc.online)  
**Version:** 1.0.0  
**Minecraft:** 1.21.6+  
**Fabric:** 0.17.2+