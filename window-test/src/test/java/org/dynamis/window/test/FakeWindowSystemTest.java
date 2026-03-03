package org.dynamis.window.test;

import org.dynamis.window.api.BackendHint;
import org.dynamis.window.api.InputEvent;
import org.dynamis.window.api.SurfaceType;
import org.dynamis.window.api.Window;
import org.dynamis.window.api.WindowConfig;
import org.dynamis.window.api.WindowEvent;
import org.dynamis.window.api.WindowEvents;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeWindowSystemTest {

    @Test
    void fakeWindowEmitsDeterministicQueuedEvents() {
        FakeWindowSystem system = new FakeWindowSystem();
        Window window = system.create(new WindowConfig("fake", 640, 480, false, true, BackendHint.AUTO));
        FakeWindow fake = (FakeWindow) window;

        fake.pushResize(800, 600);
        fake.pushInputEvent(new InputEvent.Key(65, 0, InputEvent.InputAction.PRESS, 0));
        fake.requestClose();

        WindowEvents events = fake.pollEvents();

        assertEquals(2, events.windowEvents().size());
        assertEquals(1, events.inputEvents().size());
        assertTrue(fake.shouldClose());
    }

    @Test
    void fakeWindowSurfaceLifecycleIsStable() {
        FakeWindowSystem system = new FakeWindowSystem();
        FakeWindow window = (FakeWindow) system.create(new WindowConfig("fake", 320, 240, false, true, BackendHint.VULKAN));

        var surface = window.createSurface(SurfaceType.VULKAN);
        assertEquals(surface.value(), window.getVulkanSurfaceHandle());

        window.destroySurface(surface);
        assertEquals(0L, window.getVulkanSurfaceHandle());
    }

    @Test
    void pollEventsDrainsQueue() {
        FakeWindowSystem system = new FakeWindowSystem();
        FakeWindow window = (FakeWindow) system.create(WindowConfig.defaults());

        window.pushWindowEvent(new WindowEvent.FocusChanged(true));
        assertFalse(window.pollEvents().windowEvents().isEmpty());
        assertTrue(window.pollEvents().windowEvents().isEmpty());
    }
}
