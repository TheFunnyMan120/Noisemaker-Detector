package com.precdev.noisemakerdetector.detection;

import com.precdev.noisemakerdetector.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ScanScheduler {
    private final ContraptionScanner scanner = new ContraptionScanner();
    private final ScanCache cache = new ScanCache();
    private int tickCounter = 0;
    private BlockPos lastScanPos = BlockPos.ZERO;
    private boolean forceScan = false;
    private static final int MOVE_THRESHOLD_SQ = 16 * 16; // 16 blocks

    public void tick(Level level, Player player) {
        tickCounter++;

        boolean shouldScan = forceScan
                || tickCounter >= ConfigManager.getConfig().scanIntervalTicks
                || player.blockPosition().distSqr(lastScanPos) >= MOVE_THRESHOLD_SQ;

        if (!shouldScan) return;

        forceScan = false;
        tickCounter = 0;
        lastScanPos = player.blockPosition();
        cache.update(scanner.scan(level, player));
    }

    public void requestRescan() {
        forceScan = true;
    }

    public ScanCache getCache() {
        return cache;
    }
}
