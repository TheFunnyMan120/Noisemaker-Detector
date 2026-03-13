package com.precdev.noisemakerdetector.detection;

public class ScanCache {
    private volatile ScanResult lastResult;

    public void update(ScanResult result) {
        this.lastResult = result;
    }

    public ScanResult getLastResult() {
        return lastResult;
    }

    public void invalidate() {
        this.lastResult = null;
    }
}
