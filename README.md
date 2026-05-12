# YoHAM

A Fabric mod for Minecraft 26.1.1 that adds realistic ham radio communication mechanics, including proximity voice chat, Morse code, signal propagation, and a progression system.

## Features

- **Proximity Voice Chat** — Built on Simple Voice Chat for realistic near-field communication
- **Ham Radio System** — Handheld radios, base stations, and repeaters with realistic range limits
- **Morse Code (CW)** — Send and receive Morse code using keybinds or audio tones
- **Signal Propagation** — Line-of-sight, ionospheric skip, and terrain-based signal attenuation
- **Player Sectors** — Division-based communication zones with cross-sector radio support
- **Propagation Events** — Dynamic weather effects like solar storms and sporadic E openings
- **Progression Tree** — Unlock better radios, antennas, and repeaters through crafting

## Installation

### For Players

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 26.1.1
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat) (required for voice features)
4. Download the latest YoHAM `.jar` from releases and place it in your `mods` folder

### For Developers

**Requirements:**
- JDK 25+
- Git

**Clone and build:**
```bash
git clone https://github.com/CpreForEver/YoHAM.git
cd YoHAM
./gradlew build
```

The compiled `.jar` will be in `build/libs/`.

**Run in development:**
```bash
./gradlew runClient    # Run the mod in a dev client
./gradlew runServer     # Run a dev server
```

## Usage

### Radio Item
The basic Radio item is available in the Ingredients creative tab. Right-click to use it (functionality to be expanded).

### Controls
- **Right-click** — Use held radio / open GUI
- **Push-to-talk** — Configurable in Key Mappings (default: depends on Simple Voice Chat)

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Fabric API | 0.145.3+26.1.1 | Core modding framework |
| Simple Voice Chat | 2.6.15+26.1.1 | Proximity voice chat foundation |

## Building from Source

```bash
# Full build with tests
./gradlew build

# Build only the jar
./gradlew jar

# Run client for testing
./gradlew runClient

# Run server for testing
./gradlew runServer
```

## Project Structure

```
src/
├── main/java/          — Server-side code
│   ├── yoham/mc/       — Mod entry point, items, events
│   └── yoham/mc/mixin/ — Server mixins
├── client/java/        — Client-side code
│   └── yoham/mc/client/ — Client entry point, mixins
└── resources/          — Config, assets, translations
```

## License

This project is licensed under CC0-1.0 (Public Domain).
