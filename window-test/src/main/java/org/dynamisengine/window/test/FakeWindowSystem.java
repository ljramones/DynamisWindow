package org.dynamisengine.window.test;

import org.dynamisengine.window.api.Window;
import org.dynamisengine.window.api.WindowConfig;
import org.dynamisengine.window.api.WindowHandle;
import org.dynamisengine.window.api.WindowSystem;

import java.util.concurrent.atomic.AtomicLong;

public final class FakeWindowSystem implements WindowSystem {
    private static final AtomicLong NEXT_HANDLE = new AtomicLong(1);

    @Override
    public Window create(WindowConfig config) {
        return new FakeWindow(new WindowHandle(NEXT_HANDLE.getAndIncrement()), config);
    }
}
