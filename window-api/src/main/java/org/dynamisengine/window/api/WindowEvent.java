package org.dynamisengine.window.api;

public sealed interface WindowEvent permits WindowEvent.Resized, WindowEvent.CloseRequested, WindowEvent.FocusChanged {

    record Resized(WindowSize size) implements WindowEvent {
    }

    record CloseRequested() implements WindowEvent {
    }

    record FocusChanged(boolean focused) implements WindowEvent {
    }
}
