package org.dynamisengine.window.glfw;

import org.dynamisengine.window.api.BackendHint;
import org.dynamisengine.window.api.InputEvent;
import org.dynamisengine.window.api.SurfaceHandle;
import org.dynamisengine.window.api.SurfaceType;
import org.dynamisengine.window.api.Window;
import org.dynamisengine.window.api.WindowConfig;
import org.dynamisengine.window.api.WindowEvent;
import org.dynamisengine.window.api.WindowEvents;
import org.dynamisengine.window.api.WindowHandle;
import org.dynamisengine.window.api.WindowSize;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFWVulkan.nglfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public final class GlfwWindow implements Window {
    private final WindowHandle handle;
    private final long windowPtr;
    private final BackendHint backendHint;

    private final ConcurrentLinkedQueue<WindowEvent> windowEvents = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InputEvent> inputEvents = new ConcurrentLinkedQueue<>();

    private WindowSize windowSize;
    private WindowSize framebufferSize;
    private long vulkanInstanceHandle = VK_NULL_HANDLE;
    private long vulkanSurfaceHandle = VK_NULL_HANDLE;

    private GLFWWindowSizeCallback windowSizeCallback;
    private GLFWFramebufferSizeCallback framebufferSizeCallback;
    private GLFWWindowFocusCallback focusCallback;
    private GLFWWindowCloseCallback closeCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWScrollCallback scrollCallback;

    GlfwWindow(WindowHandle handle, WindowConfig config) {
        this.handle = handle;
        this.backendHint = config.backendHint();

        configureWindowHints(config);
        this.windowPtr = glfwCreateWindow(config.width(), config.height(), config.title(), 0L, 0L);
        if (windowPtr == 0L) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        if (backendHint == BackendHint.OPENGL) {
            glfwMakeContextCurrent(windowPtr);
            glfwSwapInterval(config.vSyncEnabled() ? 1 : 0);
        }

        this.windowSize = queryWindowSize();
        this.framebufferSize = queryFramebufferSize();
        installCallbacks();
        glfwShowWindow(windowPtr);
    }

    @Override
    public WindowHandle handle() {
        return handle;
    }

    @Override
    public WindowEvents pollEvents() {
        glfwPollEvents();

        List<WindowEvent> drainedWindowEvents = new ArrayList<>();
        List<InputEvent> drainedInputEvents = new ArrayList<>();

        drain(windowEvents, drainedWindowEvents);
        drain(inputEvents, drainedInputEvents);

        return new WindowEvents(drainedWindowEvents, drainedInputEvents);
    }

    @Override
    public void swapBuffers() {
        if (backendHint == BackendHint.OPENGL || backendHint == BackendHint.AUTO) {
            glfwSwapBuffers(windowPtr);
        }
    }

    @Override
    public long getVulkanSurfaceHandle() {
        return vulkanSurfaceHandle;
    }

    public long createVulkanSurface(long instanceHandle) {
        if (instanceHandle <= 0) {
            throw new IllegalArgumentException("instanceHandle must be positive");
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.mallocLong(1);
            int result = nglfwCreateWindowSurface(instanceHandle, windowPtr, 0L, memAddress(pSurface));
            if (result != VK_SUCCESS) {
                throw new IllegalStateException("Failed to create Vulkan surface, VkResult=" + result);
            }
            this.vulkanInstanceHandle = instanceHandle;
            this.vulkanSurfaceHandle = pSurface.get(0);
            return vulkanSurfaceHandle;
        }
    }

    @Override
    public WindowSize framebufferSize() {
        return framebufferSize;
    }

    @Override
    public WindowSize windowSize() {
        return windowSize;
    }

    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowPtr);
    }

    @Override
    public void close() {
        glfwSetWindowShouldClose(windowPtr, true);
        freeCallbacks();
        glfwDestroyWindow(windowPtr);
    }

    @Override
    public SurfaceHandle createSurface(SurfaceType type) {
        return switch (type) {
            case OPENGL -> new SurfaceHandle(windowPtr);
            case VULKAN -> {
                if (vulkanSurfaceHandle == VK_NULL_HANDLE) {
                    throw new IllegalStateException("No Vulkan surface handle available. Use createVulkanSurface(long) first.");
                }
                yield new SurfaceHandle(vulkanSurfaceHandle);
            }
        };
    }

    @Override
    public void destroySurface(SurfaceHandle handle) {
        if (handle.value() == vulkanSurfaceHandle) {
            vulkanInstanceHandle = VK_NULL_HANDLE;
            vulkanSurfaceHandle = VK_NULL_HANDLE;
        }
    }

    private void configureWindowHints(WindowConfig config) {
        glfwWindowHint(GLFW_RESIZABLE, config.resizable() ? GLFW_TRUE : GLFW_FALSE);
        if (config.backendHint() == BackendHint.VULKAN) {
            glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        } else {
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        }
    }

    private WindowSize queryWindowSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(windowPtr, width, height);
            return new WindowSize(width.get(0), height.get(0));
        }
    }

    private WindowSize queryFramebufferSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(windowPtr, width, height);
            return new WindowSize(width.get(0), height.get(0));
        }
    }

    private void installCallbacks() {
        windowSizeCallback = glfwSetWindowSizeCallback(windowPtr, (window, width, height) -> {
            windowSize = new WindowSize(width, height);
            windowEvents.add(new WindowEvent.Resized(windowSize));
        });
        framebufferSizeCallback = glfwSetFramebufferSizeCallback(windowPtr, (window, width, height) -> {
            framebufferSize = new WindowSize(width, height);
        });
        focusCallback = glfwSetWindowFocusCallback(windowPtr,
                (window, focused) -> windowEvents.add(new WindowEvent.FocusChanged(focused)));
        closeCallback = glfwSetWindowCloseCallback(windowPtr,
                window -> windowEvents.add(new WindowEvent.CloseRequested()));

        keyCallback = glfwSetKeyCallback(windowPtr,
                (window, key, scancode, action, mods) -> inputEvents.add(
                        new InputEvent.Key(key, scancode, mapAction(action), mods)));
        mouseButtonCallback = glfwSetMouseButtonCallback(windowPtr,
                (window, button, action, mods) -> inputEvents.add(
                        new InputEvent.MouseButton(button, mapAction(action), mods)));
        cursorPosCallback = glfwSetCursorPosCallback(windowPtr,
                (window, xPos, yPos) -> inputEvents.add(new InputEvent.CursorMoved(xPos, yPos)));
        scrollCallback = glfwSetScrollCallback(windowPtr,
                (window, xOffset, yOffset) -> inputEvents.add(new InputEvent.Scroll(xOffset, yOffset)));
    }

    private static InputEvent.InputAction mapAction(int glfwAction) {
        return switch (glfwAction) {
            case 1 -> InputEvent.InputAction.PRESS;
            case 2 -> InputEvent.InputAction.REPEAT;
            default -> InputEvent.InputAction.RELEASE;
        };
    }

    private static <T> void drain(ConcurrentLinkedQueue<T> queue, List<T> destination) {
        for (T item = queue.poll(); item != null; item = queue.poll()) {
            destination.add(item);
        }
    }

    private void freeCallbacks() {
        if (windowSizeCallback != null) {
            windowSizeCallback.free();
        }
        if (framebufferSizeCallback != null) {
            framebufferSizeCallback.free();
        }
        if (focusCallback != null) {
            focusCallback.free();
        }
        if (closeCallback != null) {
            closeCallback.free();
        }
        if (keyCallback != null) {
            keyCallback.free();
        }
        if (mouseButtonCallback != null) {
            mouseButtonCallback.free();
        }
        if (cursorPosCallback != null) {
            cursorPosCallback.free();
        }
        if (scrollCallback != null) {
            scrollCallback.free();
        }
    }
}
