module org.dynamisengine.window.glfw {
    requires transitive org.dynamisengine.window.api;
    requires org.dynamisengine.worldengine.api;
    requires org.dynamisengine.worldengine.runtime;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.vulkan;

    exports org.dynamisengine.window.glfw;
}
