package org.dynamis.window.api;

import java.util.List;

public record WindowEvents(List<WindowEvent> windowEvents, List<InputEvent> inputEvents) {
    public WindowEvents {
        windowEvents = List.copyOf(windowEvents);
        inputEvents = List.copyOf(inputEvents);
    }

    public static WindowEvents empty() {
        return new WindowEvents(List.of(), List.of());
    }
}
