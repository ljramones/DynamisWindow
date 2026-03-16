module org.dynamisengine.window.glfw {
    requires transitive org.dynamisengine.window.api;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.vulkan;

    exports org.dynamisengine.window.glfw;
}
