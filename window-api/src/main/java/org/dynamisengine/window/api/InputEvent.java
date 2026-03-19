package org.dynamisengine.window.api;

/**
 * Raw platform input events delivered by DynamisWindow.
 *
 * Consumed by DynamisInput for normalization, mapping, and frame snapshot production.
 * DynamisWindow owns raw platform capture; DynamisInput owns semantic mapping.
 */
public sealed interface InputEvent permits
        InputEvent.Key, InputEvent.MouseButton, InputEvent.CursorMoved, InputEvent.Scroll,
        InputEvent.GamepadButton, InputEvent.GamepadAxis, InputEvent.GamepadConnected,
        InputEvent.GamepadDisconnected {

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

    /**
     * Gamepad button press/release event.
     *
     * @param gamepadId platform-assigned gamepad index (0-based)
     * @param button    button identifier (see {@link GamepadButtons} for standard mapping)
     * @param action    PRESS or RELEASE (no REPEAT for gamepad buttons)
     */
    record GamepadButton(int gamepadId, int button, InputAction action) implements InputEvent {
    }

    /**
     * Gamepad analog axis value change.
     *
     * @param gamepadId platform-assigned gamepad index (0-based)
     * @param axis      axis identifier (see {@link GamepadAxes} for standard mapping)
     * @param value     raw axis value in [-1.0, 1.0] for sticks, [0.0, 1.0] for triggers
     */
    record GamepadAxis(int gamepadId, int axis, float value) implements InputEvent {
    }

    /** Gamepad connected event. Fired when a gamepad is plugged in or recognized. */
    record GamepadConnected(int gamepadId, String name) implements InputEvent {
    }

    /** Gamepad disconnected event. Fired when a gamepad is unplugged or lost. */
    record GamepadDisconnected(int gamepadId) implements InputEvent {
    }
}
