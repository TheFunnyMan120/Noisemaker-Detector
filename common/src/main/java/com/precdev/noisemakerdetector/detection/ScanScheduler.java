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

        int interval = ConfigManager.getConfig().scanIntervalTicks;
        boolean shouldScan = false;

        if (forceScan) {
            shouldScan = true;
            forceScan = false;
        } else if (tickCounter >= interval) {
            shouldScan = true;
        } else if (player.blockPosition().distSqr(lastScanPos) >= MOVE_THRESHOLD_SQ) {
            shouldScan = true;
        }

        if (shouldScan) {
            tickCounter = 0;
            lastScanPos = player.blockPosition();
            ScanResult result = scanner.scan(level, player);
            cache.update(result);
        }
    }

    public void requestRescan() {
        forceScan = true;
    }

    public ScanCache getCache() {
        return cache;
    }
}
