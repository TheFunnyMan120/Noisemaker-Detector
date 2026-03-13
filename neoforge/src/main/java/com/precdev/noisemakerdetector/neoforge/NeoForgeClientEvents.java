package com.precdev.noisemakerdetector.neoforge;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.render.ContraptionRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class NeoForgeClientEvents {
    public static void register() {
        NeoForge.EVENT_BUS.register(NeoForgeClientEvents.class);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        NoisemakerDetectorClient.onClientTick(net.minecraft.client.Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
        ContraptionRenderer.render(event.getPoseStack(), 0f);
    }
}
