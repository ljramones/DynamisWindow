package org.dynamisengine.window.glfw;

import org.dynamisengine.window.api.*;
import org.dynamisengine.worldengine.api.WorldContext;
import org.dynamisengine.worldengine.api.lifecycle.DynamisInitException;
import org.dynamisengine.worldengine.api.lifecycle.DynamisShutdownException;
import org.dynamisengine.worldengine.api.lifecycle.DynamisTickException;
import org.dynamisengine.worldengine.api.telemetry.SubsystemHealth;
import org.dynamisengine.worldengine.runtime.subsystem.WorldSubsystem;

import java.util.Optional;
import java.util.Set;

/**
 * WorldSubsystem adapter for GLFW window lifecycle.
 *
 * <p>Bridges the DynamisWindow GLFW backend into the WorldEngine
 * subsystem lifecycle: creates the window on init, polls events each
 * tick, and shuts down on engine stop.
 */
public final class GlfwWindowSubsystem implements WorldSubsystem {

    private GlfwWindowSystem windowSystem;
    private Window window;
    private volatile WindowEvents lastEvents = WindowEvents.empty();
    private volatile boolean closeRequested = false;
    private volatile boolean initialized = false;

    private final String title;
    private final int width;
    private final int height;
    private final BackendHint backendHint;

    public GlfwWindowSubsystem(String title, int width, int height) {
        this(title, width, height, BackendHint.OPENGL);
    }

    public GlfwWindowSubsystem(String title, int width, int height, BackendHint backendHint) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.backendHint = backendHint;
    }

    @Override public String name() { return "Window"; }
    @Override public Set<String> dependencies() { return Set.of(); }

    @Override
    public void initialize(WorldContext context) throws DynamisInitException {
        windowSystem = new GlfwWindowSystem();
        window = windowSystem.create(new WindowConfig(title, width, height, false, true, backendHint));
        initialized = true;
    }

    @Override public void start() {}

    @Override
    public void tick(long tick, float deltaSeconds) throws DynamisTickException {
        if (window != null) {
            lastEvents = window.pollEvents();
            if (window.shouldClose()) closeRequested = true;
        }
    }

    @Override public void stop() {}

    @Override
    public void shutdown() throws DynamisShutdownException {
        if (window != null) { window.close(); window = null; }
        if (windowSystem != null) { windowSystem.shutdown(); windowSystem = null; }
        initialized = false;
    }

    @Override
    public SubsystemHealth health() {
        if (!initialized) return SubsystemHealth.absent(name());
        return SubsystemHealth.healthy(name(), 0);
    }

    @Override
    public Optional<Object> captureTelemetry() { return Optional.empty(); }

    public WindowEvents lastEvents() { return lastEvents; }
    public boolean isCloseRequested() { return closeRequested; }
    public Window window() { return window; }
}
