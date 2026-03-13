# Changelog

## v1.0.0

### Detection Algorithm Overhaul

**Problem:** The original proximity-based detection produced massive false positives in villages and normal builds. Every door near a button, every bell, every fence gate was getting flagged.

**Root causes identified:**
1. **Proximity radius was too loose** — checking "is there redstone within N blocks" caught unrelated redstone near normal structures
2. **Buttons, levers, pistons, dispensers, etc. were classified as redstone** — these appear in normal builds everywhere
3. **`hasNeighborSignal()` triggered on momentary power** — a button press on a village door counted as "connected to redstone"
4. **`minNoisemakersPerContraption` was 1** — a single door near a single button was enough to trigger

**Solution: Adjacency + dual-threshold detection**

The new algorithm has three key changes:

1. **Direct adjacency replaces proximity radius.** A noisemaker must be *directly touching* (26 neighbors, Chebyshev distance 1) a circuit redstone block to be flagged. Redstone can only power blocks it physically contacts — if a door isn't touching circuit wiring, it cannot be part of a contraption.

2. **Circuit-only redstone classification.** Only automated circuit components count: redstone wire, repeaters, comparators, observers, redstone torches, powered blocks (block of redstone). Removed: buttons, levers, pistons, dispensers, droppers, sculk sensors, daylight detectors, target blocks. These are either manual triggers or blocks common in non-malicious builds.

3. **Dual-threshold filtering.** A connected group is flagged as a contraption if it has `>= 3 noisemakers` OR `>= 4 circuit redstone blocks`. This catches both multi-door spam (many noisemakers, small circuit) and single-noteblock clocks (few noisemakers, large circuit) while filtering out everything in normal gameplay.

**Why this eliminates village false positives:** Vanilla villages have doors, bells, fence gates, and trapdoors — but zero circuit redstone (wire, repeaters, comparators, observers, torches) adjacent to them. The adjacency check filters them all out before grouping even begins.
