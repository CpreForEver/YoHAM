# YoHAM — Design Document

> **Status:** Draft v0.1  
> **Purpose:** Guide for implementing ham radio communication mechanics using Simple Voice Chat (SVC) as the foundation

---

## 1. Simple Voice Chat — What It Provides Out of the Box

SVC gives us a working proximity voice chat system. Here's what we can leverage directly:

### 1.1 Built-in Features (No Code Needed)
- **Proximity voice chat** — players hear each other when within range (configurable, default ~20 blocks)
- **Push-to-talk / voice activation** — built into SVC client
- **3D positional audio** — audio direction and volume based on player position
- **Groups** — isolated, open, or password-protected voice groups
- **Volume categories** — per-category volume control (music, voice, etc.)
- **Opus encoding** — efficient audio compression
- **Noise suppression** — RNNoise built-in

### 1.2 API Capabilities (What We Can Code)

| Feature | API Class/Method | Use for YoHAM |
|---------|-----------------|----------------|
| **Audio Senders** | `AudioSender` | Programmatic audio (Morse tones, radio static, alerts) |
| **Locational Audio** | `LocationalAudioChannel` | Radio sounds at a position (base station, repeater) |
| **Static Audio** | `StaticAudioChannel` | Audio to specific players (broadcast, alert) |
| **Entity Audio** | `EntityAudioChannel` | Audio from placed entities (radio blocks) |
| **Groups** | `Group` | Sector-based voice groups, radio networks |
| **Volume Categories** | `VolumeCategory` | Radio audio, CW tones, static noise categories |
| **Audio Encoder/Decoder** | `api.createEncoder()` / `api.createDecoder()` | Encode/decode Morse tones, radio effects |
| **Player Connections** | `api.getConnectionOf(uuid)` | Check if player has SVC installed |

### 1.3 What SVC Does NOT Do (We Must Implement)
- ~~Frequency selection~~ — all SVC voice is "wideband"
- ~~Signal range based on equipment~~ — SVC uses fixed proximity
- ~~Interference/static~~ — SVC audio is clean
- ~~CW/Morse code encoding~~ — needs programmatic audio
- ~~Sector restrictions~~ — needs custom logic
- ~~Propagation effects~~ — ionosphere, terrain blocking, altitude
- ~~Power/battery systems~~ — game mechanic, not audio

---

## 2. Communication Use Cases

### 2.1 Close Range (0–50 blocks) — No Radio Required

**Scenario:** Players standing near each other in a village or base.

| Method | Range | When Used | Notes |
|--------|-------|-----------|-------|
| Normal voice chat | ~20 blocks | Always available | SVC proximity, no equipment needed |
| Whispering | ~5 blocks | Secret conversations | SVC whisper mode, or voice chat groups |

**Implementation:** Use SVC's built-in proximity. No YoHAM code needed.

### 2.2 Short Range (50–200 blocks) — Handheld Radio

**Scenario:** Players exploring nearby areas, mining in different tunnels, or scouts reporting back.

| Method | Range | Equipment | Mode |
|--------|-------|-----------|------|
| Handheld radio (VHF) | 50–150 blocks | Handheld radio | SSB or FM |
| Handheld radio (UHF) | 100–200 blocks | Handheld radio + antenna | SSB |

**When to use radio vs voice:**
- Voice: Players within ~20 blocks, in same building/area
- Radio: Players beyond voice range but in same general area (same sector, nearby buildings)

**Implementation:**
- Create a radio item that, when right-clicked, opens a radio GUI
- Player selects frequency, turns on transmission
- When transmitting, audio is routed through a `LocationalAudioChannel` or `Group`
- Range limited by radio power + frequency band

### 2.3 Medium Range (200–1000 blocks) — Base Station

**Scenario:** Base-to-base communication, town-to-town, or long-distance mining operations.

| Method | Range | Equipment | Mode |
|--------|-------|-----------|------|
| Base station (HF) | 200–800 blocks | Base station + antenna | SSB |
| Base station (HF) + repeater | 500–1000 blocks | Base station + antenna + repeater | SSB |

**Implementation:**
- Base station block placed at fixed location
- Players tune to the base station's frequency
- Uses `StaticAudioChannel` to route audio between connected players
- Range affected by antenna height, terrain, and propagation conditions

### 2.4 Long Range (1000+ blocks) — HF Skip / Cross-Dimension

**Scenario:** Inter-base communication, cross-dimension messaging, or global broadcasts.

| Method | Range | Equipment | Mode |
|--------|-------|-----------|------|
| HF skip (ionospheric) | 1000–5000 blocks | HF rig + large antenna | SSB or CW |
| Cross-dimension | Unlimited | HF rig + special antenna | CW or digital |
| Repeater network | 500–2000 per hop | Multiple repeaters | SSB |

**Implementation:**
- HF skip uses `LocationalAudioChannel` with extended distance
- Ionospheric bounce calculated based on time of day, solar conditions
- CW mode for weak signals (narrower bandwidth = clearer at distance)

---

## 3. Radio Frequencies and Bands

### 3.1 Band Overview

| Band | Frequency | Typical Use | YoHAM Range | Best Mode |
|------|-----------|-------------|-------------|-----------|
| **HF** | 3–30 MHz | Long-distance, skip propagation | 500–5000 blocks | SSB, CW |
| **VHF** | 30–300 MHz | Local, line-of-sight | 50–300 blocks | SSB, FM |
| **UHF** | 300–3000 MHz | Short-range, penetration | 100–500 blocks | FM, SSB |

### 3.2 Specific Frequencies for YoHAM

**Handheld Radios (VHF/UHF):**
- `146.520 MHz` — Simplex calling (national simplex frequency)
- `145.000 MHz` — FM simplex (general use)
- `146.400 MHz` — FM repeater input/output

**Base Stations (HF):**
- `14.200 MHz` — HF SSB calling (20m band)
- `7.150 MHz` — HF SSB calling (40m band)
- `28.500 MHz` — HF SSB calling (10m band)

**CW (Morse Code) Frequencies:**
- `14.050 MHz` — CW calling (20m band)
- `7.050 MHz` — CW calling (40m band)
- `1.850 MHz` — CW calling (160m band)

### 3.3 When to Use Which Mode

| Situation | Recommended Mode | Why |
|-----------|-----------------|-----|
| Close range, clear signals | FM (VHF/UHF) | Easier to understand, built-in squelch |
| Medium range, general use | SSB (VHF/UHF) | Better range than FM, clearer than AM |
| Long range, weak signals | SSB (HF) | Most efficient for distance |
| Very long range, poor conditions | CW (HF) | Narrowest bandwidth, works in noise |
| Emergency, low power | CW | Works at lowest power levels |

---

## 4. CW (Morse Code) Design

### 4.1 CW Specifications

| Parameter | Value | Notes |
|-----------|-------|-------|
| **Bandwidth** | ~150 Hz | Narrowest practical mode |
| **Speed** | 10–30 WPM | Words per minute (standard is 20 WPM) |
| **Dot length** | 50 ms at 20 WPM | Base timing unit |
| **Dash length** | 150 ms | 3x dot length |
| **Intra-element space** | 0 ms | Between dots/dashes within a character |
| **Inter-character space** | 150 ms | 3x dot length |
| **Inter-word space** | 500 ms | 7x dot length |

### 4.2 CW Implementation Options

**Option A: Keybind-based (Simple)**
- Player holds a key to send dots, taps to send dashes
- Or: one key for dot, another for dash
- Simple to implement, realistic feel

**Option B: Audio-tone input (Complex)**
- Player speaks into microphone, audio analyzed for tone patterns
- Detects dot/dash patterns from voice
- Very complex, may not be worth the effort

**Option C: Hybrid (Recommended)**
- Default: keybind-based for simplicity
- Optional: audio-tone input as an advanced feature
- GUI shows Morse code being sent/received

### 4.3 CW Audio Generation

Use SVC's `AudioSender` to generate Morse tones:
1. Convert text to Morse code using `morse.txt` lookup table
2. Generate sine wave tones at the selected frequency
3. Encode to Opus using `api.createEncoder()`
4. Send audio packets via `AudioSender`
5. Time packets correctly (don't send too fast)

---

## 5. Signal Propagation Calculations

### 5.1 Line-of-Sight (VHF/UHF)

**Formula:**
```
Distance (blocks) = √(2 × R × h1) + √(2 × R × h2)
```
Where:
- `R` = effective Earth radius (~8490 km for radio waves, simplified to ~500,000 blocks)
- `h1` = height of transmitter (blocks above ground)
- `h2` = height of receiver (blocks above ground)

**Simplified for Minecraft:**
```
Distance ≈ √(h1) × 20 + √(h2) × 20
```

**Examples:**
- Both players at ground level (h=0): Distance = 0 (blocked by terrain)
- One on hill (h=100), one at ground: Distance ≈ 200 blocks
- Both on towers (h=200): Distance ≈ 565 blocks

### 5.2 Ionospheric Skip (HF)

**Skip Distance:**
```
Skip Distance = 2 × √(h_ionosphere² + (d/2)²)
```
Where:
- `h_ionosphere` = ionosphere height (~300 km, simplified to ~10,000 blocks)
- `d` = ground distance between stations

**Simplified:**
```
Min Skip Distance ≈ 2000 blocks (at lowest HF frequencies)
Max Skip Distance ≈ 5000 blocks (at highest HF frequencies)
```

**Day/Night Effects:**
- **Daytime:** Higher frequencies work better (10m, 15m, 20m bands)
- **Nighttime:** Lower frequencies work better (40m, 80m, 160m bands)
- **Dusk/Dawn:** Transition period, both bands may work

### 5.3 Attenuation (Signal Loss)

**Free Space Path Loss:**
```
FSPL (dB) = 20 × log10(d) + 20 × log10(f) + 20 × log10(4π/c)
```

**Simplified for Minecraft:**
```
Signal Loss (dB) = 20 × log10(distance / 10) + frequency_penalty
```

Where:
- `distance` = blocks between stations
- `frequency_penalty` = higher frequency = more loss
  - HF: 0 dB penalty
  - VHF: +10 dB penalty
  - UHF: +20 dB penalty

**Noise Floor:**
- Base noise floor: -100 dB
- Solar storm noise floor: -60 dB (HF only)
- Static from terrain: +10 to +30 dB depending on environment

### 5.4 Power and Range

**Transmitter Power Levels:**

| Device | Power (Watts) | Typical Range (VHF) | Typical Range (HF) |
|--------|--------------|---------------------|-------------------|
| Handheld radio | 5W | 50–150 blocks | Not supported |
| Mobile radio | 25W | 100–300 blocks | 500–1000 blocks |
| Base station | 100W | 200–500 blocks | 1000–3000 blocks |
| HF rig + antenna | 500W | 300–800 blocks | 2000–5000 blocks |

**Antenna Gains:**

| Antenna Type | Gain (dBi) | Effect |
|-------------|-----------|--------|
| No antenna (rubber duck) | 0 dBi | Baseline |
| Vertical whip | 2 dBi | +20% range |
| Yagi (directional) | 6 dBi | +50% range in one direction |
| Dipole | 3 dBi | +25% range |
| Large array | 10 dBi | +100% range |

---

## 6. Game Design Elements

### 6.1 Player Sectors

**Purpose:** Limit initial communication range to encourage progression and exploration.

**Proposed Model — Grid-based:**
- World divided into 500×500 block sectors
- Each player starts in a random sector
- Players can communicate within their sector using voice chat
- Cross-sector communication requires radio equipment

**Sector Assignment:**
- Random assignment at world creation
- Players can see their sector on a minimap or HUD
- Sector coordinates stored in player data

### 6.2 Progression Tree

**Tier 1: Novice (Starting)**
- No radio equipment
- Communication: Voice chat only (~20 blocks)
- Unlocks: Crafting recipes for handheld radio

**Tier 2: Technician**
- Handheld radio (VHF/UHF)
- Communication: 50–200 blocks
- Unlocks: Antenna crafting, base station recipes

**Tier 3: General**
- Base station (HF)
- Communication: 500–1000 blocks
- Unlocks: Repeater placement, CW mode

**Tier 4: Amateur Extra**
- HF rig + large antenna
- Communication: 1000–5000 blocks
- Unlocks: Cross-dimension communication, repeater networks

### 6.3 Propagation Events

| Event | Effect | Duration | Rarity |
|-------|--------|----------|--------|
| **Clear** | Normal propagation | Always | 35% |
| **Fair** | Slightly reduced range | 1–2 in-game days | 25% |
| **Poor** | Significant range reduction | 2–4 in-game days | 20% |
| **Solar Storm** | HF noise floor increases, range drops | 3–7 in-game days | 15% |
| **Sporadic E** | VHF range increases temporarily | 0.5–1 in-game day | 5% |
| **Blackout** | No HF propagation | 5+ in-game days | Rare |

### 6.4 Radio Items

**Handheld Radio:**
- Right-click to open GUI
- Select frequency (VHF/UHF presets)
- Push-to-talk keybind
- Battery-powered (depletes over time)
- Range: 50–200 blocks

**Base Station:**
- Placed block
- Connected to power source
- Fixed frequency (configurable)
- Range: 500–1000 blocks
- Can connect to antennas for extended range

**Repeater:**
- Placed block
- Boosts signal for other radios
- Range extension: +200 blocks per repeater
- Requires power source

---

## 7. Implementation Priorities

### Phase 1: Foundation
1. ~~SVC integration~~ (done)
2. Radio item with basic GUI
3. Frequency selection (VHF/UHF presets)
4. Push-to-talk via SVC audio channel

### Phase 2: Range and Propagation
5. Line-of-sight range calculation
6. Terrain blocking (simple)
7. Antenna system for range extension

### Phase 3: Advanced Features
8. CW/Morse code support
9. Ionospheric skip (HF bands)
10. Propagation events (solar storms, etc.)

### Phase 4: Progression and Depth
11. Sector system
12. Progression tree
13. Repeater network
14. Cross-dimension communication

---

## 8. Key Decisions Needed

1. **Radio item interaction:** Right-click to open GUI, or hold to transmit?
2. **CW input method:** Keybind-based, audio-tone, or hybrid?
3. **Sector system:** Grid-based, region-based, or player-assigned?
4. **Power system:** Battery-driven, or connected to world power?
5. **Frequency selection:** Preset channels, or full frequency dial?
6. **Progression:** Crafting-based, XP-based, or both?

---

*Document created to guide YoHAM development. Update as design decisions are made.*
