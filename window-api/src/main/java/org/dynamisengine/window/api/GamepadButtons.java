package org.dynamisengine.window.api;

/**
 * Standard gamepad button identifiers.
 *
 * Follows the SDL/XInput "Xbox" layout convention. Platform backends
 * map their native button indices to these constants.
 *
 * Button values are indices into the GLFW gamepad state array for
 * compatibility, but the mapping is platform-independent.
 */
public final class GamepadButtons {

    private GamepadButtons() {}

    /** A / Cross (bottom face button) */
    public static final int A = 0;
    /** B / Circle (right face button) */
    public static final int B = 1;
    /** X / Square (left face button) */
    public static final int X = 2;
    /** Y / Triangle (top face button) */
    public static final int Y = 3;
    /** Left bumper / L1 */
    public static final int LEFT_BUMPER = 4;
    /** Right bumper / R1 */
    public static final int RIGHT_BUMPER = 5;
    /** Back / Select / Share */
    public static final int BACK = 6;
    /** Start / Options / Menu */
    public static final int START = 7;
    /** Guide / Home / PS */
    public static final int GUIDE = 8;
    /** Left stick click / L3 */
    public static final int LEFT_THUMB = 9;
    /** Right stick click / R3 */
    public static final int RIGHT_THUMB = 10;
    /** D-pad up */
    public static final int DPAD_UP = 11;
    /** D-pad right */
    public static final int DPAD_RIGHT = 12;
    /** D-pad down */
    public static final int DPAD_DOWN = 13;
    /** D-pad left */
    public static final int DPAD_LEFT = 14;

    /** Total number of standard buttons. */
    public static final int COUNT = 15;
}
