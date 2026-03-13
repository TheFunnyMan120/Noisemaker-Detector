package com.precdev.noisemakerdetector.render;

public class RenderConstants {
    public static final float BEAM_RADIUS_INNER = 0.1f;
    public static final float BEAM_RADIUS_OUTER = 0.15f;
    public static final int BEAM_HEIGHT = 256;

    // Extract ARGB components from packed color int
    public static float red(int color) {
        return ((color >> 16) & 0xFF) / 255.0f;
    }

    public static float green(int color) {
        return ((color >> 8) & 0xFF) / 255.0f;
    }

    public static float blue(int color) {
        return (color & 0xFF) / 255.0f;
    }

    public static float alpha(int color) {
        return ((color >> 24) & 0xFF) / 255.0f;
    }
}
