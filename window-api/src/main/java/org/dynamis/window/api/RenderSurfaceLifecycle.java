package org.dynamis.window.api;

public interface RenderSurfaceLifecycle {
    SurfaceHandle createSurface(SurfaceType type);

    void destroySurface(SurfaceHandle handle);
}
