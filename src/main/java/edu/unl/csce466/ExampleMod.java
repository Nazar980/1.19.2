package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ExampleMod {
    public static final String MODID = "examplemod";

    private static boolean showGui = false;

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    // GLFW callback — храним оригинальный
    private static GLFWWindowRefreshCallback originalRefreshCallback;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);

        // Устанавливаем callback на window refresh (вызывается после swap в Render thread)
        setupGlfwCallback();
    }

    private void setupGlfwCallback() {
        long window = Minecraft.getInstance().getWindow().getWindow();

        // Сохраняем оригинальный refresh callback (если был)
        originalRefreshCallback = GLFW.glfwSetWindowRefreshCallback(window, (long win) -> {
            // Вызываем оригинальный callback (если был)
            if (originalRefreshCallback != null) {
                originalRefreshCallback.invoke(win);
            }

            // Теперь рендерим ImGui — контекст активен!
            renderImGui();
        });
    }

    // Toggle на F
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
        }
    }

    private static void renderImGui() {
        if (!showGui) return;

        if (!initialized) {
            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null);

            long window = Minecraft.getInstance().getWindow().getWindow();
            imGuiGlfw.init(window, true);
            imGuiGl3.init("#version 150");

            // Обязательно строим шрифты — фикс assertion
            io.getFonts().build();

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized in GLFW refresh callback!");
        }

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("ExampleMod ImGui (no Mixin)");
        ImGui.text("Привет! Работает через GLFW refresh callback");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.separator();
        ImGui.text("F — toggle");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
