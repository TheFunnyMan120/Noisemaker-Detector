package com.precdev.noisemakerdetector.detection;

import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.config.ModConfig;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class BlockClassifier {
    public enum BlockType {
        NOISEMAKER,
        REDSTONE,
        IRRELEVANT
    }

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

    public static BlockType classify(BlockState state) {
        Block block = state.getBlock();

        if (isNoisemaker(block)) {
            return BlockType.NOISEMAKER;
        }
        if (isRedstone(block)) {
            return BlockType.REDSTONE;
        }
        return BlockType.IRRELEVANT;
    }

    private static boolean isNoisemaker(Block block) {
        ModConfig config = ConfigManager.getConfig();

        if (config.detectDoors && block instanceof DoorBlock) return true;
        if (config.detectTrapdoors && block instanceof TrapDoorBlock) return true;
        if (config.detectBells && block instanceof BellBlock) return true;
        if (config.detectNoteBlocks && block instanceof NoteBlock) return true;
        if (config.detectFenceGates && block instanceof FenceGateBlock) return true;

        return false;
    }

    private static boolean isRedstone(Block block) {
        for (Class<? extends Block> clazz : REDSTONE_CLASSES) {
            if (clazz.isInstance(block)) return true;
        }
        return false;
    }

    public static String getNoisemakerType(Block block) {
        if (block instanceof DoorBlock) return "door";
        if (block instanceof TrapDoorBlock) return "trapdoor";
        if (block instanceof BellBlock) return "bell";
        if (block instanceof NoteBlock) return "note_block";
        if (block instanceof FenceGateBlock) return "fence_gate";
        return "unknown";
    }
}
