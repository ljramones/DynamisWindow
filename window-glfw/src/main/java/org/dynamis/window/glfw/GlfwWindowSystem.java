package org.dynamis.window.glfw;

import org.dynamis.window.api.Window;
import org.dynamis.window.api.WindowConfig;
import org.dynamis.window.api.WindowHandle;
import org.dynamis.window.api.WindowSystem;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public final class GlfwWindowSystem implements WindowSystem {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final AtomicLong NEXT_HANDLE = new AtomicLong(1);

    private GLFWErrorCallback errorCallback;

    @Override
    public Window create(WindowConfig config) {
        initializeGlfwIfNeeded();
        return new GlfwWindow(new WindowHandle(NEXT_HANDLE.getAndIncrement()), config);
    }

    private void initializeGlfwIfNeeded() {
        if (INITIALIZED.get()) {
            return;
        }

        synchronized (INITIALIZED) {
            if (INITIALIZED.get()) {
                return;
            }

            errorCallback = GLFWErrorCallback.createPrint(System.err);
            glfwSetErrorCallback(errorCallback);

            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            INITIALIZED.set(true);
        }
    }

    public void shutdown() {
        if (!INITIALIZED.get()) {
            return;
        }

        synchronized (INITIALIZED) {
            if (!INITIALIZED.get()) {
                return;
            }

            glfwTerminate();
            if (errorCallback != null) {
                errorCallback.free();
                errorCallback = null;
            }
            INITIALIZED.set(false);
        }
    }
}
