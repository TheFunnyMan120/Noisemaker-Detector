package com.precdev.noisemakerdetector.detection;

import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.config.ModConfig;
import net.minecraft.world.level.block.*;

import java.util.Set;

public class BlockClassifier {

    // Only automated circuit components — NOT manual triggers (buttons, levers)
    // or blocks common in normal builds (pistons, dispensers, droppers)
    private static final Set<Class<? extends Block>> REDSTONE_CLASSES = Set.of(
            RedStoneWireBlock.class,
            RepeaterBlock.class,
            ComparatorBlock.class,
            ObserverBlock.class,
            RedstoneTorchBlock.class,
            RedstoneWallTorchBlock.class,
            PoweredBlock.class
    );

    public static boolean isRedstone(Block block) {
        for (Class<? extends Block> clazz : REDSTONE_CLASSES) {
            if (clazz.isInstance(block)) return true;
        }
        return false;
    }

    /**
     * Returns the noisemaker type string if the block is an enabled noisemaker,
     * or null if it is not.
     */
    public static String getNoisemakerType(Block block) {
        ModConfig config = ConfigManager.getConfig();

        if (config.detectDoors && block instanceof DoorBlock) return "door";
        if (config.detectTrapdoors && block instanceof TrapDoorBlock) return "trapdoor";
        if (config.detectBells && block instanceof BellBlock) return "bell";
        if (config.detectNoteBlocks && block instanceof NoteBlock) return "note_block";
        if (config.detectFenceGates && block instanceof FenceGateBlock) return "fence_gate";
        return null;
    }
}
