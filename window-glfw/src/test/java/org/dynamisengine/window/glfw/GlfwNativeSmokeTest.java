package org.dynamisengine.window.glfw;

import org.dynamisengine.window.api.BackendHint;
import org.dynamisengine.window.api.Window;
import org.dynamisengine.window.api.WindowConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlfwNativeSmokeTest {

    @Test
    @EnabledIfSystemProperty(named = "dynamis.window.nativeSmoke", matches = "true")
    void canCreateAndCloseWindow() {
        GlfwWindowSystem system = new GlfwWindowSystem();

        Window window = system.create(new WindowConfig("smoke", 320, 240, false, true, BackendHint.OPENGL));
        assertNotNull(window);

        window.close();
        system.shutdown();
    }
}
