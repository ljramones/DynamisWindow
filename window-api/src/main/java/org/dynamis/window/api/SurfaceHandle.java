package org.dynamis.window.api;

public record SurfaceHandle(long value) {
    public SurfaceHandle {
        if (value <= 0) {
            throw new IllegalArgumentException("SurfaceHandle value must be positive");
        }
    }
}
