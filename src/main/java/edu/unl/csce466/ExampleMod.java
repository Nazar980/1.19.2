package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.flag.ImGuiConfigFlags;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod("examplemod")
public class ExampleMod {
    private static boolean showGui = false;

    private static boolean initialized = false;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return; // Не трогаем в меню

        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
            System.out.println("[ExampleMod] GUI toggled: " + showGui);
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // Только после основного HUD
        if (!event.getOverlay().id().getNamespace().equals("minecraft") ||
            !event.getOverlay().id().getPath().equals("all")) {
            return;
        }

        if (!showGui) return;

        // Защита от другого потока
        if (!Thread.currentThread().getName().equals("Render thread")) {
            System.err.println("[ImGui] Wrong thread: " + Thread.currentThread().getName());
            return;
        }

        if (!initialized) {
            System.out.println("[ExampleMod] Starting ImGui initialization...");

            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null);
            io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

            long window = Minecraft.getInstance().getWindow().getWindow();

            imGuiGlfw = new ImGuiImplGlfw();
            imGuiGl3 = new ImGuiImplGl3();

            imGuiGlfw.init(window, true);
            imGuiGl3.init("#version 150");

            // Шрифты — обязательно после init
            io.getFonts().addFontDefault();
            io.getFonts().build();

            // Тёмная тема + скругление
            ImGui.styleColorsDark();
            ImGui.getStyle().setWindowRounding(8.0f);

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized successfully!");
        }

        // Сохраняем и сбрасываем GL-состояние Minecraft
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // 🔥 Тестовое демо-окно — чтобы сразу увидеть, что рендер работает
        ImGui.showDemoWindow();

        // Твоё окно — принудительно в центре
        ImGui.setNextWindowPos(200, 200);
        ImGui.setNextWindowSize(500, 400);
        ImGui.begin("ExampleMod GUI");
        ImGui.text("Если ты это видишь — всё работает!");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.text("F — toggle");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // Восстанавливаем состояние
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
    }
}
