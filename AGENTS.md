# YoHAM Minecraft Plugin — Development Skill

## Project Overview

**YoHAM** is a Fabric Minecraft mod (ham radio simulation) targeting Minecraft 26.1.1 with Java 25. It adds realistic radio communications, voice chat integration, and a progression system to Minecraft.

**Tech stack:** Fabric Loader, Fabric API, Gradle 9.4.0, Loom 1.15.5, Java 25

**Mod ID:** `yoham`

---

## Architecture

```
src/
├── main/java/youham/mc/
│   ├── Yoham.java              — ModInitializer (entry point)
│   └── mixin/ExampleMixin.java — Server-side mixin (hooks into MinecraftServer.loadLevel)
├── client/java/youham/mc/client/
│   ├── YohamClient.java        — ClientModInitializer
│   └── mixin/                  — Client-side mixins
├── resources/
│   └── morse.txt               — Morse code lookup table
└── fabric.mod.json             — Mod manifest (in resources/)

build.gradle                    — Gradle build config
gradle.properties               — Version properties
```

**Source sets:** Split client/server via `loom.splitEnvironmentSourceSets()` — main for server, client for client-side code.

---

## Build System

**Key files:**
- `build.gradle` — Fabric Loom plugin, Fabric API dependency
- `gradle.properties` — Version constants

**Current versions (gradle.properties):**
```
minecraft_version=26.1.1
loader_version=0.18.6
loom_version=1.15-SNAPSHOT
fabric_api_version=0.145.3+26.1.1
mod_version=1.0.0
maven_group=youham.mc
```

**Run build:** `./gradlew build`
**Run dev:** `./gradlew runClient` or `./gradlew runServer`

---

## Dependencies

**Required:**
- Fabric API (full) — `net.fabricmc.fabric-api:fabric-api:0.145.3+26.1.1`
  - Modular artifacts (`fabric-networking-api-v1`, etc.) are not published separately at this version
  - Consider switching to modular deps when available for this Minecraft version

**Planned / Optional:**
- Simple Voice Chat — for proximity voice chat and programmatic audio
  - API: `de.maxhenkel.voicechat:voicechat-api:2.6.13` (from `https://maven.maxhenkel.de/repository/public`)
  - Runtime: `maven.modrinth:simple-voice-chat:fabric-2.6.15+26.1.1` (from `https://api.modrinth.com/maven`)
  - API stub: `de.maxhenkel.voicechat:voicechat-api:2.6.13:fabric-stub`
  - Plugin entrypoint: `YohamVoicechatPlugin` implements `VoicechatPlugin`
  - Depends on: `voicechat_api >= 2.6.13` in fabric.mod.json

---

## Fabric API — Key APIs for This Project

### Networking
- `ServerPlayNetworking` / `ClientPlayNetworking` — send/receive packets between client and server
- `ServerLoginNetworking` — login-phase custom data exchange
- Use for: sync radio state, signal data, player sector info

### Events (Callback Interfaces)
- `ServerLifecycleEvents` — `ServerStarted`, `ServerStopping`, `ServerTickEvents`
- `ServerPlayerEvents` — `PlayerLoggedIn`, `PlayerLoggedOut`, `PlayerRemoved`
- `PlayerConnectionEvents` — connection lifecycle
- `UseBlockCallback` / `UseEntityCallback` — item use interactions
- Register via `Event.register(callback)` pattern

### Commands
- `CommandRegistrationCallback` — register Brigadier commands
- `ClientCommandRegistrationCallback` — client-side commands
- Use for: `/yoham`, `/hamradio`, sector management, testing

### Registry
- `RegistryEvents.MODIFY` — add custom items, blocks, entities
- Register via `Registry.register(Registry.ITEM, new Identifier(...), newItem)`

### Mixins
- SpongePowered ASM-based bytecode injection
- Use when Fabric API events don't cover a hook point
- Example: `ExampleMixin` injects into `MinecraftServer.loadLevel()`

---

## Simple Voice Chat API — Key Concepts

**Plugin entrypoint:** Implement `de.maxhenkel.voicechat.api.VoicechatPlugin`
- Register in `fabric.mod.json` under `"entrypoints": {"voicechat": ["your.PluginClass"]}`
- Required method: `getPluginId()` — return `"yoham"`
- `initialize(VoicechatApi api)` — get API reference

**Key interfaces:**
- `VoicechatServerApi` — server-side operations
- `VoicechatClientApi` — client-side operations
- `VoicechatApi` — base interface with common methods

**Key classes for this project:**
- `AudioSender` — register a player as an audio sender (for programmatic voice)
  - `sender.send(opusData)` — send audio packets
  - `sender.whispering(true)` — whisper mode
  - `sender.reset()` — end stream
- `LocationalAudioChannel` — play audio at a position (for radio sounds)
- `StaticAudioChannel` — play audio to specific players/groups
- `Group` — voice chat groups (isolated, open, password-protected)
- `VolumeCategory` — per-category volume control

**Audio:**
- `api.createEncoder()` / `api.createDecoder()` — Opus encode/decode
- `api.getVoiceChatDistance()` — default proximity radius
- Audio is Opus-encoded; packets must be timed correctly (don't send too fast)

**Integration pattern:**
1. Player speaks → capture audio → encode to Opus → send via `AudioSender` or route through channel
2. Player receives → decode Opus → play through OpenAL with proximity/attenuation

---

## Game Design — Features

### 1. Proximity Voice Chat (via Simple Voice Chat)
- Use SVC's built-in proximity system as the foundation
- Override or extend with YoHAM-specific rules (sector restrictions, signal interference)
- Default SVC distance is configurable; YoHAM may apply modifiers based on equipment

### 2. Morse Code Global Voice Chat
- Global voice chat converted to Morse code audio tones
- Uses `morse.txt` lookup table for encoding
- Input: keybind-based dot/dash or audio-tone input
- Output: Morse code audio played through voice channels
- Realistic: dots (·) = short tone, dashes (—) = long tone, inter-element silence, inter-character silence

### 3. Player Sectors
- Each player starts in a different sector
- Sectors limit voice/radio range unless equipment supports cross-sector communication
- Division method: TBD (grid-based, region-based, or world sections)
- Assignment: random, by spawn, or player choice

### 4. Simulated Radio Propagation
- **Line-of-sight (VHF/UHF):** Distance = √(2 × radius × height1) + √(2 × radius × height2) — simplified to Minecraft blocks
- **Ionospheric skip (HF):** Signal bounces off ionosphere layer; daytime vs nighttime affects max distance
- **Altitude bonus:** Higher elevation = better propagation
- **Gains:** Antenna height multiplier, terrain penetration factor, repeater boost
- **Attenuation:** Signal strength decreases with distance; noise floor determines minimum usable signal

### 5. Communication Devices
- **Handheld radios:** Short range, battery-powered, limited frequency bands
- **Base stations:** Long range, fixed power source, antenna-compatible
- **Mobile receivers:** Medium range, vehicle-mounted
- **Underground / repeaters:** Signal penetration, boost factors, placed blocks

### 6. Progression Tree
- Slow progression tied to crafting and building infrastructure
- Unlock tiers: basic handheld → better radios → antennas → repeaters → HF rigs
- Each tier adds: range, frequency bands, device types, perks

### 7. Propagation Events (Dynamic)
- **Solar storms:** Increase noise floor on HF, reduce range
- **Sporadic E:** Temporary VHF long-range openings
- **Day/night cycle:** Affects ionospheric propagation
- Event states: Blackout → Poor → Fair → Average → Good → Great (with rarity weights)

---

## Existing Assets

- `resources/morse.txt` — Morse code lookup table (A-Z, 0-9)
- `ExampleMixin.java` — Server mixin hooking `MinecraftServer.loadLevel()` — good template for new server-side mixins
- Split source sets already configured for client/server separation

---

## Development Guidelines

### Code Style
- Package: `youham.mc` (main), `youham.mc.client` (client), `youham.mc.mixin` (mixins)
- Logger: `Yoham.LOGGER` (slf4j, mod-id named)
- Use Fabric API events/callbacks over mixins when possible
- Mixins only when Fabric API doesn't provide a hook

### File Locations
- Server code: `src/main/java/youham/mc/`
- Client code: `src/client/java/youham/mc/client/`
- Mixins: `src/main/java/youham/mc/mixin/` (server) and `src/client/java/youham/mc/client/mixin/` (client)
- Resources: `resources/` (auto-extracted to jar root)
- Generated data: `src/main/generated/` (if datagen enabled)

### Important Notes
- Minecraft 26.1 uses **official Mojang mappings** (not Yarn) — API names match Mojang's obfuscated names
- Fabric API 26.1 has renamed classes to match official mappings — check migration docs when updating
- Audio packets must be timed correctly — don't send too fast or they'll be discarded
- Voice Chat UDP port: 24454 (default) — needs to be open on server
- Always unregister `AudioSender` when done to free the player slot

---

## Key URLs (Quick Reference)

| Resource | URL |
|----------|-----|
| Fabric Docs | https://docs.fabricmc.net/ |
| Fabric API GitHub | https://github.com/FabricMC/fabric-api |
| Fabric API Migration (26.1) | https://docs.fabricmc.net/develop/porting/fabric-api |
| Simple Voice Chat API | https://dev.modrepo.de/minecraft/voicechat/api/overview |
| SVC API Examples | https://dev.modrepo.de/minecraft/voicechat/api/examples |
| Radiocraft (reference mod) | https://radiocraft.w1btr.com/ |
| Radiocraft GitHub | https://github.com/hammcmod/RadioCraft |

---

## TODO / Pending Decisions

1. **Fabric API modularity** — full jar works now; switch to modular deps when published for 26.1.1
2. **Sector division method** — grid, region, or world sections
3. **Propagation formula specifics** — exact constants for LOS, skip distance, attenuation
4. **Progression tree design** — specific tiers, unlock conditions, balance
5. **Morse code input method** — keybind vs audio-tone vs hybrid
6. **Device crafting recipes** — what materials, what tiers
