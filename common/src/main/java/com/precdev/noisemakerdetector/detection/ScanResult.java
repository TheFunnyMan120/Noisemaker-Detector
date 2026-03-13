package com.precdev.noisemakerdetector.detection;

import java.util.List;

public class ScanResult {
    private final List<Contraption> contraptions;
    private final long timestamp;

    public ScanResult(List<Contraption> contraptions) {
        this.contraptions = contraptions;
        this.timestamp = System.currentTimeMillis();
    }

    public List<Contraption> getContraptions() {
        return contraptions;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isEmpty() {
        return contraptions.isEmpty();
    }
}
