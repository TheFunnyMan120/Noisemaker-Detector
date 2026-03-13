package com.precdev.noisemakerdetector.neoforge;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.keybind.ModKeybinds;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod("noisemakerdetector")
public class NoisemakerDetectorNeoForge {
    public NoisemakerDetectorNeoForge(IEventBus modBus) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NoisemakerDetectorClient.init();

            // Register the render pipeline during FMLClientSetupEvent, which fires
            // after mod construction but before Minecraft.<init>() and shader compilation.
            // Registering it in the @Mod constructor is too early and causes an NPE
            // in ModelManager.reload() because RenderPipelines class loading triggers
            // a cascade of rendering subsystem initialization before the game is ready.
            modBus.addListener((FMLClientSetupEvent event) -> {
                event.enqueueWork(NoisemakerDetectorClient::initRenderer);
            });

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
