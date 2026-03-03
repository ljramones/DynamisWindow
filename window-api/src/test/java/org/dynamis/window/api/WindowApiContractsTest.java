package org.dynamis.window.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WindowApiContractsTest {

    @Test
    void defaultsAreSane() {
        WindowConfig defaults = WindowConfig.defaults();

        assertEquals("Dynamis Window", defaults.title());
        assertEquals(1280, defaults.width());
        assertEquals(720, defaults.height());
        assertTrue(defaults.vSyncEnabled());
        assertTrue(defaults.resizable());
        assertEquals(BackendHint.AUTO, defaults.backendHint());
    }

    @Test
    void configRejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> new WindowConfig("   ", 1, 1, true, true, BackendHint.AUTO));
    }

    @Test
    void configRejectsInvalidDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> new WindowConfig("ok", 0, 10, true, true, BackendHint.AUTO));
        assertThrows(IllegalArgumentException.class,
                () -> new WindowConfig("ok", 10, -1, true, true, BackendHint.AUTO));
    }

    @Test
    void configRejectsNullBackendHint() {
        assertThrows(NullPointerException.class,
                () -> new WindowConfig("ok", 10, 10, true, true, null));
    }

    @Test
    void windowHandleMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new WindowHandle(0));
        assertEquals(99L, new WindowHandle(99L).value());
    }

    @Test
    void surfaceHandleMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new SurfaceHandle(-5));
        assertEquals(7L, new SurfaceHandle(7L).value());
    }

    @Test
    void windowSizeMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new WindowSize(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new WindowSize(1, 0));
    }

    @Test
    void windowEventsDefensiveCopyPreventsFundamentalDuplicationByMutation() {
        List<WindowEvent> windowEvents = new ArrayList<>();
        windowEvents.add(new WindowEvent.CloseRequested());

        WindowEvents snapshot = new WindowEvents(windowEvents, List.of());
        windowEvents.add(new WindowEvent.CloseRequested());

        assertEquals(1, snapshot.windowEvents().size());
    }

    @Test
    void emptyEventsContainNoPayload() {
        WindowEvents empty = WindowEvents.empty();

        assertTrue(empty.windowEvents().isEmpty());
        assertTrue(empty.inputEvents().isEmpty());
        assertFalse(empty.windowEvents() == null || empty.inputEvents() == null);
    }
}
