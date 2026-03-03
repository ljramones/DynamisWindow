package org.dynamis.window.api;

public sealed interface InputEvent permits InputEvent.Key, InputEvent.MouseButton, InputEvent.CursorMoved, InputEvent.Scroll {

    enum InputAction {
        PRESS,
        RELEASE,
        REPEAT
    }

    record Key(int keyCode, int scanCode, InputAction action, int modifiers) implements InputEvent {
    }

    record MouseButton(int button, InputAction action, int modifiers) implements InputEvent {
    }

    record CursorMoved(double x, double y) implements InputEvent {
    }

    record Scroll(double offsetX, double offsetY) implements InputEvent {
    }
}
