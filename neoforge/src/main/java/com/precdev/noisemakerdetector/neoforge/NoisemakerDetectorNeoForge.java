package com.precdev.noisemakerdetector.neoforge;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.keybind.ModKeybinds;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod("noisemakerdetector")
public class NoisemakerDetectorNeoForge {
    public NoisemakerDetectorNeoForge(IEventBus modBus) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NoisemakerDetectorClient.init();

            // Register keybinds on mod bus
            modBus.addListener((RegisterKeyMappingsEvent event) -> {
                for (KeyMapping key : ModKeybinds.createKeyMappings()) {
                    event.register(key);
                }
            });

            NeoForgeClientEvents.register();
        }
    }
}
