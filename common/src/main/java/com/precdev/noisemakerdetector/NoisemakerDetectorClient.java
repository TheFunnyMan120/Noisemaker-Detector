package com.precdev.noisemakerdetector;

import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.detection.ScanScheduler;
import com.precdev.noisemakerdetector.keybind.ModKeybinds;
import com.precdev.noisemakerdetector.render.ContraptionRenderer;
import net.minecraft.client.Minecraft;

public class NoisemakerDetectorClient {
    private static ScanScheduler scanScheduler;
    private static boolean enabled = true;

    public static void init() {
        ConfigManager.load();
        scanScheduler = new ScanScheduler();
    }

    /**
     * Force-loads ContraptionRenderer so the custom RenderPipeline is registered
     * before shader compilation. Must be called after mod construction but before
     * Minecraft's initial resource reload.
     * <p>
     * On Fabric: call from {@code onInitializeClient()}.
     * On NeoForge: call from {@code FMLClientSetupEvent} (fires before Minecraft init).
     */
    public static void initRenderer() {
        ContraptionRenderer.init();
    }

    public static void onClientTick(Minecraft client) {
        if (client.level == null || client.player == null) return;

        ModKeybinds.handleInput(client);

        if (enabled) {
            scanScheduler.tick(client.level, client.player);
        }
    }

    public static ScanScheduler getScanScheduler() {
        return scanScheduler;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void toggleEnabled() {
        enabled = !enabled;
    }
}
