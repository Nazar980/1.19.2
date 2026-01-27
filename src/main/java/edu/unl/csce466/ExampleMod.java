package edu.unl.csce466;

import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod("examplemod")
public class ExampleMod {

    private static boolean showGui = false;
    private static boolean lastFState = false;

    private static boolean imguiInit = false;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void initImGui() {
        if (imguiInit) return;

        long windowHandle = Minecraft.getInstance().getWindow().getWindow();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        // ❗❗❗ ОБЯЗАТЕЛЬНО
        io.getFonts().addFontDefault();
        io.getFonts().build();

        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 150");

        imguiInit = true;
        System.out.println("[ExampleMod] ImGui initialized");
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        long window = mc.getWindow().getWindow();

        // ===== GLFW EDGE CHECK =====
        boolean fPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
        if (fPressed && !lastFState) {
            showGui = !showGui;
        }
        lastFState = fPressed;

        if (!showGui) return;

        initImGui();

        RenderSystem.disableDepthTest();

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("ExampleMod");
        ImGui.text("ImGui работает");
        ImGui.text("Открывается по F");
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        RenderSystem.enableDepthTest();
    }
}
