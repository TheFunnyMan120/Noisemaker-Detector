package com.precdev.noisemakerdetector.detection;

import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class ContraptionScanner {

    public ScanResult scan(Level level, Player player) {
        ModConfig config = ConfigManager.getConfig();
        int radius = config.detectionRadius;

        // Phase 1: Block Collection
        Set<BlockPos> allRedstone = new HashSet<>();
        Map<BlockPos, String> noisemakerTypeMap = new HashMap<>();

        collectBlocks(level, player.blockPosition(), radius, allRedstone, noisemakerTypeMap);

        if (noisemakerTypeMap.isEmpty() || allRedstone.isEmpty()) {
            return new ScanResult(Collections.emptyList());
        }

        // Phase 2: Adjacency Filtering
        // A noisemaker is suspicious ONLY if it directly touches a circuit redstone block.
        Set<BlockPos> suspiciousNoisemakers = new HashSet<>();
        for (BlockPos pos : noisemakerTypeMap.keySet()) {
            if (hasAdjacentRedstone(pos, allRedstone)) {
                suspiciousNoisemakers.add(pos);
            }
        }

        if (suspiciousNoisemakers.isEmpty()) {
            return new ScanResult(Collections.emptyList());
        }

        // Phase 3: BFS Grouping
        List<Contraption> contraptions = groupContraptions(suspiciousNoisemakers, allRedstone,
                noisemakerTypeMap);

        // Phase 4: Dual-threshold filtering
        // A group is a real contraption if it has EITHER:
        //   - enough noisemakers (multiple doors/bells = spam), OR
        //   - enough circuit redstone (real automated circuit, even with fewer noisemakers)
        contraptions.removeIf(c ->
                c.getNoisemakerPositions().size() < config.minNoisemakersPerContraption
                && c.getRedstonePositions().size() < config.minRedstonePerContraption);

        return new ScanResult(contraptions);
    }

    private void collectBlocks(Level level, BlockPos center, int radius,
                               Set<BlockPos> redstone, Map<BlockPos, String> typeMap) {
        int minX = center.getX() - radius;
        int minY = Math.max(level.getMinY(), center.getY() - radius);
        int minZ = center.getZ() - radius;
        int maxX = center.getX() + radius;
        int maxY = Math.min(level.getMaxY(), center.getY() + radius);
        int maxZ = center.getZ() + radius;

        int minChunkX = SectionPos.blockToSectionCoord(minX);
        int maxChunkX = SectionPos.blockToSectionCoord(maxX);
        int minChunkZ = SectionPos.blockToSectionCoord(minZ);
        int maxChunkZ = SectionPos.blockToSectionCoord(maxZ);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = level.getChunk(cx, cz);

                int chunkMinX = cx << 4;
                int chunkMinZ = cz << 4;

                LevelChunkSection[] sections = chunk.getSections();
                for (int sIdx = 0; sIdx < sections.length; sIdx++) {
                    LevelChunkSection section = sections[sIdx];
                    if (section == null || section.hasOnlyAir()) continue;

                    int sectionBaseY = level.getMinY() + (sIdx << 4);
                    if (sectionBaseY > maxY || sectionBaseY + 15 < minY) continue;

                    for (int dx = 0; dx < 16; dx++) {
                        int bx = chunkMinX + dx;
                        if (bx < minX || bx > maxX) continue;

                        for (int dz = 0; dz < 16; dz++) {
                            int bz = chunkMinZ + dz;
                            if (bz < minZ || bz > maxZ) continue;

                            for (int dy = 0; dy < 16; dy++) {
                                int by = sectionBaseY + dy;
                                if (by < minY || by > maxY) continue;

                                Block block = section.getBlockState(dx, dy, dz).getBlock();
                                String noisemakerType = BlockClassifier.getNoisemakerType(block);

                                if (noisemakerType != null) {
                                    typeMap.put(new BlockPos(bx, by, bz), noisemakerType);
                                } else if (BlockClassifier.isRedstone(block)) {
                                    redstone.add(new BlockPos(bx, by, bz));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasAdjacentRedstone(BlockPos pos, Set<BlockPos> redstone) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (redstone.contains(pos.offset(dx, dy, dz))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Contraption> groupContraptions(Set<BlockPos> suspiciousNoisemakers,
                                                 Set<BlockPos> allRedstone,
                                                 Map<BlockPos, String> noisemakerTypeMap) {
        Set<BlockPos> visited = new HashSet<>();
        List<Contraption> contraptions = new ArrayList<>();

        for (BlockPos startPos : suspiciousNoisemakers) {
            if (visited.contains(startPos)) continue;

            Set<BlockPos> groupNoisemakers = new HashSet<>();
            Map<BlockPos, String> groupTypes = new HashMap<>();
            Set<BlockPos> groupRedstone = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();

            queue.add(startPos);
            visited.add(startPos);

            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();

                if (suspiciousNoisemakers.contains(current)) {
                    groupNoisemakers.add(current);
                    groupTypes.put(current, noisemakerTypeMap.getOrDefault(current, "unknown"));
                } else if (allRedstone.contains(current)) {
                    groupRedstone.add(current);
                }

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            BlockPos neighbor = current.offset(dx, dy, dz);
                            if (visited.contains(neighbor)) continue;

                            if (allRedstone.contains(neighbor) || suspiciousNoisemakers.contains(neighbor)) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }

            if (!groupNoisemakers.isEmpty()) {
                contraptions.add(new Contraption(groupNoisemakers, groupTypes, groupRedstone));
            }
        }

        return contraptions;
    }
}
