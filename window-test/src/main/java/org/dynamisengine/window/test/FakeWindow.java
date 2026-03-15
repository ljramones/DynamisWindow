package org.dynamisengine.window.test;

import org.dynamisengine.window.api.InputEvent;
import org.dynamisengine.window.api.SurfaceHandle;
import org.dynamisengine.window.api.SurfaceType;
import org.dynamisengine.window.api.Window;
import org.dynamisengine.window.api.WindowConfig;
import org.dynamisengine.window.api.WindowEvent;
import org.dynamisengine.window.api.WindowEvents;
import org.dynamisengine.window.api.WindowHandle;
import org.dynamisengine.window.api.WindowSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class FakeWindow implements Window {
    private static final AtomicLong NEXT_SURFACE = new AtomicLong(1);

    private final WindowHandle handle;
    private final Queue<WindowEvent> windowEvents = new ConcurrentLinkedQueue<>();
    private final Queue<InputEvent> inputEvents = new ConcurrentLinkedQueue<>();

    private WindowSize windowSize;
    private WindowSize framebufferSize;
    private boolean shouldClose;
    private long vulkanSurfaceHandle;

    FakeWindow(WindowHandle handle, WindowConfig config) {
        this.handle = handle;
        this.windowSize = new WindowSize(config.width(), config.height());
        this.framebufferSize = this.windowSize;
    }

    @Override
    public WindowHandle handle() {
        return handle;
    }

    @Override
    public WindowEvents pollEvents() {
        List<WindowEvent> drainedWindowEvents = drain(windowEvents);
        List<InputEvent> drainedInputEvents = drain(inputEvents);
        return new WindowEvents(drainedWindowEvents, drainedInputEvents);
    }

    @Override
    public void swapBuffers() {
        // no-op in fake implementation
    }

    @Override
    public long getVulkanSurfaceHandle() {
        return vulkanSurfaceHandle;
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
        return shouldClose;
    }

    @Override
    public void close() {
        requestClose();
    }

    @Override
    public SurfaceHandle createSurface(SurfaceType type) {
        long handleValue = NEXT_SURFACE.getAndIncrement();
        if (type == SurfaceType.VULKAN) {
            vulkanSurfaceHandle = handleValue;
        }
        return new SurfaceHandle(handleValue);
    }

    @Override
    public void destroySurface(SurfaceHandle handle) {
        if (handle.value() == vulkanSurfaceHandle) {
            vulkanSurfaceHandle = 0L;
        }
    }

    public void pushWindowEvent(WindowEvent event) {
        windowEvents.add(event);
    }

    public void pushInputEvent(InputEvent event) {
        inputEvents.add(event);
    }

    public void pushResize(int width, int height) {
        this.windowSize = new WindowSize(width, height);
        this.framebufferSize = this.windowSize;
        this.windowEvents.add(new WindowEvent.Resized(windowSize));
    }

    public void requestClose() {
        this.shouldClose = true;
        this.windowEvents.add(new WindowEvent.CloseRequested());
    }

    private static <T> List<T> drain(Queue<T> queue) {
        List<T> drained = new ArrayList<>();
        for (T event = queue.poll(); event != null; event = queue.poll()) {
            drained.add(event);
        }
        return drained;
    }
}
