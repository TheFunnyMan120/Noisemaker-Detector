package com.precdev.noisemakerdetector.fabric;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.keybind.ModKeybinds;
import com.precdev.noisemakerdetector.render.ContraptionRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.KeyMapping;

public class NoisemakerDetectorFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NoisemakerDetectorClient.init();

        // Register keybinds via Fabric API
        for (KeyMapping key : ModKeybinds.createKeyMappings()) {
            KeyBindingHelper.registerKeyBinding(key);
        }

        // Client tick via Fabric API
        ClientTickEvents.END_CLIENT_TICK.register(NoisemakerDetectorClient::onClientTick);

        // Render hook
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ContraptionRenderer.render(context.matrices(), 0f);
        });
    }
}
