package com.precdev.noisemakerdetector.detection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Set;

public class Contraption {
    private final Set<BlockPos> noisemakerPositions;
    private final Map<BlockPos, String> noisemakerTypes; // pos -> type string for coloring
    private final Set<BlockPos> redstonePositions;
    private final Vec3 centroid;
    private final AABB boundingBox;

    public Contraption(Set<BlockPos> noisemakerPositions, Map<BlockPos, String> noisemakerTypes,
                       Set<BlockPos> redstonePositions) {
        this.noisemakerPositions = noisemakerPositions;
        this.noisemakerTypes = noisemakerTypes;
        this.redstonePositions = redstonePositions;
        this.centroid = computeCentroid();
        this.boundingBox = computeBoundingBox();
    }

    private Vec3 computeCentroid() {
        double x = 0, y = 0, z = 0;
        for (BlockPos pos : noisemakerPositions) {
            x += pos.getX() + 0.5;
            y += pos.getY() + 0.5;
            z += pos.getZ() + 0.5;
        }
        int count = noisemakerPositions.size();
        return new Vec3(x / count, y / count, z / count);
    }

    private AABB computeBoundingBox() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : noisemakerPositions) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX() + 1);
            maxY = Math.max(maxY, pos.getY() + 1);
            maxZ = Math.max(maxZ, pos.getZ() + 1);
        }
        for (BlockPos pos : redstonePositions) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX() + 1);
            maxY = Math.max(maxY, pos.getY() + 1);
            maxZ = Math.max(maxZ, pos.getZ() + 1);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public Set<BlockPos> getNoisemakerPositions() {
        return noisemakerPositions;
    }

    public Map<BlockPos, String> getNoisemakerTypes() {
        return noisemakerTypes;
    }

    public Set<BlockPos> getRedstonePositions() {
        return redstonePositions;
    }

    public Vec3 getCentroid() {
        return centroid;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public int getTotalBlocks() {
        return noisemakerPositions.size() + redstonePositions.size();
    }
}
