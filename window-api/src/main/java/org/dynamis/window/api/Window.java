package org.dynamis.window.api;

public interface Window extends RenderSurfaceLifecycle {
    WindowHandle handle();

    WindowEvents pollEvents();

    void swapBuffers();

    long getVulkanSurfaceHandle();

    WindowSize framebufferSize();

    WindowSize windowSize();

    boolean shouldClose();

    void close();
}
