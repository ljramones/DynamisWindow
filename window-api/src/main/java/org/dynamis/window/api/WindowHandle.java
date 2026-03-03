package org.dynamis.window.api;

public record WindowHandle(long value) {
    public WindowHandle {
        if (value <= 0) {
            throw new IllegalArgumentException("WindowHandle value must be positive");
        }
    }
}
