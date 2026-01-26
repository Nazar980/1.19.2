package edu.unl.csce466;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.Window;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;

@Mod("examplemod")
@EventBusSubscriber(modid = "examplemod", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ExampleMod {

    private static boolean showGui = false;

    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;

    public ExampleMod() {
        // Клиентская инициализация через EventBus
        net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Инициализация ImGui
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        // Используем GLFW для проверки нажатия F
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10); // простая пауза чтобы не забивать CPU
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Проверяем GLFW key
                if (GLFW.glfwGetKey(getWindowHandle(), GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS) {
                    showGui = !showGui; // переключаем
                    try {
                        Thread.sleep(200); // антидребезг
                    } catch (InterruptedException ignored) {}
                }
            }
        }).start();
    }

    // Нужно вызвать каждый рендер кадр
    public void renderGui() {
        if (!showGui) return;

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("Example Mod GUI");
        ImGui.text("ImGui включён через F!");
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private long getWindowHandle() {
        // Получаем хэндл окна Minecraft
        Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        return window.getWindow();
    }
}
