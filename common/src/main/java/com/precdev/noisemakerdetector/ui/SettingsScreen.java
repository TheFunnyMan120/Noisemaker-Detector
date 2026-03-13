package com.precdev.noisemakerdetector.ui;

import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingsScreen extends Screen {
    private final Screen parent;
    private ModConfig config;

    // Scroll state
    private int scrollOffset = 0;
    private static final int LINE_HEIGHT = 24;
    private static final int LEFT_MARGIN = 20;

    public SettingsScreen(Screen parent) {
        super(Component.literal("Noisemaker Detector Settings"));
        this.parent = parent;
        this.config = ConfigManager.getConfig();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;

        // === Detection Section ===
        // Detection Radius slider (32-128)
        addRenderableWidget(Button.builder(
                Component.literal("Detection Radius: " + config.detectionRadius),
                btn -> {
                    config.detectionRadius = cycleValue(config.detectionRadius, 16, 128, 16);
                    btn.setMessage(Component.literal("Detection Radius: " + config.detectionRadius));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT;

        // Min circuit redstone
        addRenderableWidget(Button.builder(
                Component.literal("Min Circuit Redstone: " + config.minRedstonePerContraption),
                btn -> {
                    config.minRedstonePerContraption = cycleValue(config.minRedstonePerContraption, 1, 20, 1);
                    btn.setMessage(Component.literal("Min Circuit Redstone: " + config.minRedstonePerContraption));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT;

        // Min noisemakers
        addRenderableWidget(Button.builder(
                Component.literal("Min Noisemakers: " + config.minNoisemakersPerContraption),
                btn -> {
                    config.minNoisemakersPerContraption = cycleValue(config.minNoisemakersPerContraption, 1, 10, 1);
                    btn.setMessage(Component.literal("Min Noisemakers: " + config.minNoisemakersPerContraption));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT;

        // Scan interval
        addRenderableWidget(Button.builder(
                Component.literal("Scan Interval: " + config.scanIntervalTicks + " ticks"),
                btn -> {
                    config.scanIntervalTicks = cycleValue(config.scanIntervalTicks, 10, 200, 10);
                    btn.setMessage(Component.literal("Scan Interval: " + config.scanIntervalTicks + " ticks"));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT + 8;

        // === Block Toggles ===
        addRenderableWidget(toggleButton(centerX, y, "Doors", config.detectDoors,
                val -> config.detectDoors = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Trapdoors", config.detectTrapdoors,
                val -> config.detectTrapdoors = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Bells", config.detectBells,
                val -> config.detectBells = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Note Blocks", config.detectNoteBlocks,
                val -> config.detectNoteBlocks = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Fence Gates", config.detectFenceGates,
                val -> config.detectFenceGates = val));
        y += LINE_HEIGHT + 8;

        // === Rendering ===
        addRenderableWidget(toggleButton(centerX, y, "ESP Outlines", config.espEnabled,
                val -> config.espEnabled = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Beacon Beams", config.beaconBeamsEnabled,
                val -> config.beaconBeamsEnabled = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Labels", config.labelsEnabled,
                val -> config.labelsEnabled = val));
        y += LINE_HEIGHT;

        addRenderableWidget(toggleButton(centerX, y, "Highlight Redstone", config.highlightRedstone,
                val -> config.highlightRedstone = val));
        y += LINE_HEIGHT;

        // Line width
        addRenderableWidget(Button.builder(
                Component.literal("Line Width: " + String.format("%.1f", config.lineWidth)),
                btn -> {
                    config.lineWidth = config.lineWidth >= 5.0f ? 1.0f : config.lineWidth + 0.5f;
                    btn.setMessage(Component.literal("Line Width: " + String.format("%.1f", config.lineWidth)));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT;

        // Max contraptions rendered
        addRenderableWidget(Button.builder(
                Component.literal("Max Rendered: " + config.maxContraptionsRendered),
                btn -> {
                    config.maxContraptionsRendered = cycleValue(config.maxContraptionsRendered, 10, 200, 10);
                    btn.setMessage(Component.literal("Max Rendered: " + config.maxContraptionsRendered));
                }
        ).bounds(centerX - 150, y, 300, 20).build());
        y += LINE_HEIGHT + 16;

        // === Color presets for beacon beam ===
        int colorBtnWidth = 45;
        int colorStartX = centerX - (colorBtnWidth * 3 + 12) / 2;
        int[][] presets = {
                {0xFFFF0000, 'R'}, {0xFF00FF00, 'G'}, {0xFF0000FF, 'B'},
                {0xFFFFFF00, 'Y'}, {0xFFFF00FF, 'M'}, {0xFF00FFFF, 'C'}
        };
        for (int i = 0; i < presets.length; i++) {
            final int color = presets[i][0];
            char label = (char) presets[i][1];
            int bx = colorStartX + (i % 3) * (colorBtnWidth + 4);
            int by = y + (i / 3) * (LINE_HEIGHT);
            addRenderableWidget(Button.builder(
                    Component.literal(String.valueOf(label)),
                    btn -> config.beamColor = color
            ).bounds(bx, by, colorBtnWidth, 20).build());
        }
        y += LINE_HEIGHT * 2 + 16;

        // Done button
        addRenderableWidget(Button.builder(
                Component.literal("Done"),
                btn -> onClose()
        ).bounds(centerX - 100, y, 200, 20).build());
    }

    private Button toggleButton(int centerX, int y, String label, boolean initial,
                                 java.util.function.Consumer<Boolean> setter) {
        final boolean[] value = {initial};
        return Button.builder(
                Component.literal(label + ": " + (value[0] ? "ON" : "OFF")),
                btn -> {
                    value[0] = !value[0];
                    setter.accept(value[0]);
                    btn.setMessage(Component.literal(label + ": " + (value[0] ? "ON" : "OFF")));
                }
        ).bounds(centerX - 150, y, 300, 20).build();
    }

    private int cycleValue(int current, int min, int max, int step) {
        int next = current + step;
        if (next > max) next = min;
        return next;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(parent);
    }
}
