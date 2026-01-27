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
import net.minecraftforge.event.TickEvent;
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

    private static boolean wasInGame = false; // Флаг — были ли уже в мире

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("[ExampleMod] Mod loaded — waiting for player join");
    }

    // Проверяем каждый тик, зашёл ли игрок в мир
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();

        // Если игрок появился и раньше его не было — это момент входа в мир
        if (mc.player != null && !wasInGame) {
            wasInGame = true;
            System.out.println("[ExampleMod] Player joined world — starting ImGui init");

            initImGui();
        }

        // Если вышли из мира (игрок пропал) — сбрасываем флаг
        if (mc.player == null) {
            wasInGame = false;
        }
    }

    private void initImGui() {
        if (initialized) return;

        System.out.println("[ExampleMod] ImGui initialization started...");

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

        ImGui.styleColorsDark();

        initialized = true;
        System.out.println("[ExampleMod] ImGui initialized SUCCESSFULLY!");
    }

    // Toggle на F
    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
            System.out.println("[ExampleMod] F pressed → GUI toggled: " + showGui);
        }
    }

    // Рендер ImGui каждый кадр после HUD
    @SubscribeEvent
    public void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().getNamespace().equals("minecraft") ||
            !event.getOverlay().id().getPath().equals("all")) {
            return;
        }

        if (!showGui) return;

        if (!Thread.currentThread().getName().equals("Render thread")) {
            System.err.println("[ImGui] Wrong thread: " + Thread.currentThread().getName());
            return;
        }

        if (!initialized) {
            System.out.println("[ImGui] Skip render — not initialized yet");
            return;
        }

        System.out.println("[ImGui] Rendering frame...");

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // Тестовое демо-окно — чтобы точно увидеть, что рендер идёт
        ImGui.showDemoWindow();

        // Твоё окно
        ImGui.setNextWindowPos(200, 200);
        ImGui.setNextWindowSize(600, 400);
        ImGui.begin("ExampleMod GUI");
        ImGui.text("Автоматическая инициализация при входе в мир");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.text("F — toggle");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
    }
}
