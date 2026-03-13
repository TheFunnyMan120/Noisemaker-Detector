package com.precdev.noisemakerdetector.config;

import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    // Detection
    public int detectionRadius = 64;
    public int minNoisemakersPerContraption = 3;
    public int minRedstonePerContraption = 4;
    public int scanIntervalTicks = 40;

    // Block toggles
    public boolean detectDoors = true;
    public boolean detectTrapdoors = true;
    public boolean detectBells = true;
    public boolean detectNoteBlocks = true;
    public boolean detectFenceGates = true;

    // Block colors (ARGB hex)
    public Map<String, Integer> blockColors = new HashMap<>();

    // Rendering
    public boolean espEnabled = true;
    public boolean beaconBeamsEnabled = true;
    public boolean labelsEnabled = true;
    public boolean highlightRedstone = true;
    public float lineWidth = 3.0f;
    public int beamColor = 0xFFFF0000; // Red
    public int redstoneColor = 0xFFAA00AA; // Purple
    public int maxContraptionsRendered = 50;

    // Master toggle
    public boolean modEnabled = true;

    public ModConfig() {
        // Default colors per block type
        blockColors.put("door", 0xFFFF4444);        // Red
        blockColors.put("trapdoor", 0xFFFF8800);     // Orange
        blockColors.put("bell", 0xFFFFFF00);         // Yellow
        blockColors.put("note_block", 0xFF44FF44);   // Green
        blockColors.put("fence_gate", 0xFF4488FF);   // Blue
    }

    public int getColorForBlockType(String type) {
        return blockColors.getOrDefault(type, 0xFFFFFFFF);
    }
}
