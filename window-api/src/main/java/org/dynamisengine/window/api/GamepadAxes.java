package org.dynamisengine.window.api;

/**
 * Standard gamepad axis identifiers.
 *
 * Follows the SDL/XInput convention. Stick axes range [-1.0, 1.0].
 * Trigger axes range [0.0, 1.0] (some platforms report [-1.0, 1.0]
 * for triggers — the Window backend normalizes to [0.0, 1.0]).
 */
public final class GamepadAxes {

    private GamepadAxes() {}

    /** Left stick horizontal. -1.0 = full left, +1.0 = full right. */
    public static final int LEFT_X = 0;
    /** Left stick vertical. -1.0 = full up, +1.0 = full down. */
    public static final int LEFT_Y = 1;
    /** Right stick horizontal. -1.0 = full left, +1.0 = full right. */
    public static final int RIGHT_X = 2;
    /** Right stick vertical. -1.0 = full up, +1.0 = full down. */
    public static final int RIGHT_Y = 3;
    /** Left trigger. 0.0 = released, 1.0 = fully pressed. */
    public static final int LEFT_TRIGGER = 4;
    /** Right trigger. 0.0 = released, 1.0 = fully pressed. */
    public static final int RIGHT_TRIGGER = 5;

    /** Total number of standard axes. */
    public static final int COUNT = 6;
}
