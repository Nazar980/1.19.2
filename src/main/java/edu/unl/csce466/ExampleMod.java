package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ExampleMod {
    public static final String MODID = "examplemod";

    private static boolean showGui = false;

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Toggle на F
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
            System.out.println("[ExampleMod] GUI toggled: " + showGui);
        }
    }

    // Рендер ImGui каждый кадр после HUD
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().getNamespace().equals("minecraft") ||
            !event.getOverlay().id().getPath().equals("all")) {
            return;
        }

        if (!showGui) return;

        if (!Thread.currentThread().getName().equals("Render thread")) {
            System.err.println("[ImGui] Wrong thread: " + Thread.currentThread().getName());
            return;
        }

        System.out.println("[ImGui] Rendering frame... showGui=" + showGui + ", initialized=" + initialized);

        if (!initialized) {
            System.out.println("[ExampleMod] Starting ImGui init...");

            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null);
            io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

            long window = Minecraft.getInstance().getWindow().getWindow();
            imGuiGlfw.init(window, true);
            imGuiGl3.init("#version 150");

            // Фикс assertion
            io.getFonts().build();

            // Принудительно ставим тему и позицию
            ImGui.styleColorsDark();
            ImGui.getStyle().setWindowRounding(8.0f);

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized OK! Fonts built.");
        }

        // Сохраняем и сбрасываем GL-состояние
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();  // Добавлено — иногда blend сбивает ImGui

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // 🔥 Большое демо-окно — чтобы сразу увидеть, работает ли рендер
        ImGui.showDemoWindow();

        // Твоё окно — принудительно в центре
        ImGui.setNextWindowPos(200, 200);
        ImGui.setNextWindowSize(500, 400);
        ImGui.begin("Test ImGui Window");
        ImGui.text("Если ты это видишь — GUI работает!");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.text("F — toggle");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // Восстанавливаем
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
    }
}
