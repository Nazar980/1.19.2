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
import org.lwjgl.glfw.GLFW.glfwSwapBuffers;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ExampleMod {
    public static final String MODID = "examplemod";

    private static boolean showGui = false;

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    // GLFW callback — будет вызываться каждый кадр из Render thread
    private static long originalSwapCallback = 0;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);

        // Устанавливаем свой callback на glfwSwapBuffers
        setupGlfwSwapCallback();
    }

    private static void setupGlfwSwapCallback() {
        long window = Minecraft.getInstance().getWindow().getWindow();

        // Сохраняем оригинальный callback (если был)
        originalSwapCallback = GLFW.glfwSetSwapBuffersCallback(window, (long win) -> {
            // Вызываем оригинальный swap (если был)
            if (originalSwapCallback != 0) {
                GLFW.glfwSwapBuffersCallback(originalSwapCallback).invoke(win);
            } else {
                glfwSwapBuffers(win);
            }

            // Теперь безопасно рендерим ImGui (контекст активен!)
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

            // Фикс assertion: обязательно строим шрифты
            io.getFonts().build();

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized in swap callback!");
        }

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("ExampleMod ImGui (без Mixin)");
        ImGui.text("Привет! Работает через glfwSwapBuffers");
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
