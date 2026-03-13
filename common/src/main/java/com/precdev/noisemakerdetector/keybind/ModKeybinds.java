package com.precdev.noisemakerdetector.keybind;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.ui.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    private static KeyMapping toggleKey;
    private static KeyMapping settingsKey;
    private static KeyMapping rescanKey;

    public static KeyMapping[] createKeyMappings() {
        toggleKey = new KeyMapping(
                "key.noisemakerdetector.toggle",
                GLFW.GLFW_KEY_J,
                KeyMapping.Category.MISC
        );

        settingsKey = new KeyMapping(
                "key.noisemakerdetector.settings",
                GLFW.GLFW_KEY_K,
                KeyMapping.Category.MISC
        );

        rescanKey = new KeyMapping(
                "key.noisemakerdetector.rescan",
                GLFW.GLFW_KEY_N,
                KeyMapping.Category.MISC
        );

        return new KeyMapping[]{toggleKey, settingsKey, rescanKey};
    }

    public static void handleInput(Minecraft client) {
        if (toggleKey == null) return;

        while (toggleKey.consumeClick()) {
            NoisemakerDetectorClient.toggleEnabled();
            boolean enabled = NoisemakerDetectorClient.isEnabled();
            client.player.displayClientMessage(
                    Component.literal("Noisemaker Detector: " + (enabled ? "ON" : "OFF")),
                    true
            );
        }

        while (settingsKey.consumeClick()) {
            client.setScreen(new SettingsScreen(null));
        }

        while (rescanKey.consumeClick()) {
            NoisemakerDetectorClient.getScanScheduler().requestRescan();
            client.player.displayClientMessage(
                    Component.literal("Noisemaker Detector: Rescanning..."),
                    true
            );
        }
    }
}
