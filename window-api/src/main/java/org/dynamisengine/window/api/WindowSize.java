package org.dynamisengine.window.api;

public record WindowSize(int width, int height) {
    public WindowSize {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be greater than zero");
        }
    }
}
