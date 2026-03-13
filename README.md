# Noisemaker Detector

A client-side Minecraft mod that detects and highlights redstone-powered noise-making contraptions. Built for players on multiplayer servers who want to find hidden spam machines that use doors, bells, note blocks, and other noisemakers wired to redstone circuits.

Supports **Fabric** and **NeoForge** on Minecraft **1.21.11**.

## What It Does

Scans the area around you for noisemaker blocks (doors, trapdoors, bells, note blocks, fence gates) that are directly connected to automated redstone circuitry. Detected contraptions are highlighted with:

- **ESP outlines** — colored wireframe boxes around each noisemaker block, visible through walls
- **Redstone highlighting** — purple outlines around connected circuit components (wire, repeaters, comparators, observers, torches, redstone blocks)
- **Beacon beams** — vertical lines at each contraption's center for easy spotting at a distance
- **Floating labels** — text showing the contraption breakdown (e.g. `3 door, 2 bell (7 redstone)`)

## Smart Detection

The mod doesn't flag every door or bell — only those that are part of actual redstone circuits. A village full of doors with buttons won't trigger any highlights.

**How it works:**

1. **Block collection** — scans blocks within a configurable radius using chunk-section skipping for performance
2. **Adjacency filtering** — only flags noisemakers directly touching circuit redstone (wire, repeaters, comparators, observers, redstone torches, powered blocks). Buttons, levers, and other manual triggers are ignored
3. **BFS grouping** — traces connected redstone to group noisemakers into contraptions
4. **Dual-threshold filtering** — a group must have at least 3 noisemakers OR at least 4 circuit redstone blocks to be flagged

## Keybinds

| Key | Action |
|-----|--------|
| `J` | Toggle mod on/off |
| `K` | Open settings screen |
| `N` | Force rescan |

## Settings

All settings are configurable in-game (press `K`) or via `config/noisemakerdetector.json`. On Fabric with ModMenu installed, the config screen is also accessible from the mod list.

**Detection**
- Detection radius (16–128 blocks, default 64)
- Min noisemakers per contraption (default 3)
- Min circuit redstone per contraption (default 4)
- Scan interval (default 40 ticks / 2 seconds)

**Block toggles** — enable/disable detection per block type (doors, trapdoors, bells, note blocks, fence gates)

**Rendering**
- ESP outlines on/off
- Beacon beams on/off
- Floating labels on/off
- Redstone highlighting on/off
- Line width
- Beam color (presets + configurable)
- Max contraptions rendered (default 50)

## Color Coding

| Block Type | Default Color |
|------------|---------------|
| Door | Red |
| Trapdoor | Orange |
| Bell | Yellow |
| Note Block | Green |
| Fence Gate | Blue |
| Redstone | Purple |

## Installation

Drop the JAR into your `mods` folder:

- **Fabric**: Requires [Fabric Loader](https://fabricmc.net/) 0.18+ and [Fabric API](https://modrinth.com/mod/fabric-api)
- **NeoForge**: Requires [NeoForge](https://neoforged.net/) 21.11+

## Building from Source

Requires JDK 21.

```
./gradlew build
```

JARs output to `fabric/build/libs/` and `neoforge/build/libs/`.

## License

MIT
