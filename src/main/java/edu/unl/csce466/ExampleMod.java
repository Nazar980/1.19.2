private void createStandaloneWindow() {
    new Thread(() -> {
        GLFW.glfwInit();
        long win = GLFW.glfwCreateWindow(800, 600, "ImGui Window", 0, 0);
        GLFW.glfwMakeContextCurrent(win);
        GL.createCapabilities();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);

        ImGuiImplGlfw glfwImpl = new ImGuiImplGlfw();
        ImGuiImplGl3 gl3Impl = new ImGuiImplGl3();

        glfwImpl.init(win, true);
        gl3Impl.init("#version 330 core");

        while (!GLFW.glfwWindowShouldClose(win)) {
            GLFW.glfwPollEvents();

            glfwImpl.newFrame();
            ImGui.newFrame();

            ImGui.begin("Standalone");
            ImGui.text("Это отдельное окно!");
            ImGui.end();

            ImGui.render();
            gl3Impl.renderDrawData(ImGui.getDrawData());

            GLFW.glfwSwapBuffers(win);
        }

        gl3Impl.dispose();
        glfwImpl.dispose();
        ImGui.destroyContext();
        GLFW.glfwDestroyWindow(win);
        GLFW.glfwTerminate();
    }, "ImGui Standalone").start();
}
