package org.dynamis.window.test;

import org.dynamis.window.api.Window;
import org.dynamis.window.api.WindowConfig;
import org.dynamis.window.api.WindowHandle;
import org.dynamis.window.api.WindowSystem;

import java.util.concurrent.atomic.AtomicLong;

public final class FakeWindowSystem implements WindowSystem {
    private static final AtomicLong NEXT_HANDLE = new AtomicLong(1);

    @Override
    public Window create(WindowConfig config) {
        return new FakeWindow(new WindowHandle(NEXT_HANDLE.getAndIncrement()), config);
    }
}
