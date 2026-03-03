package org.dynamis.window.api;

import java.util.Objects;

public record WindowConfig(
        String title,
        int width,
        int height,
        boolean vSyncEnabled,
        boolean resizable,
        BackendHint backendHint
) {
    public WindowConfig {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("width must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be greater than zero");
        }
        backendHint = Objects.requireNonNull(backendHint, "backendHint must not be null");
    }

    public static WindowConfig defaults() {
        return new WindowConfig("Dynamis Window", 1280, 720, true, true, BackendHint.AUTO);
    }
}
