package edu.unl.csce466;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ExampleMod {

    public static final String MODID = "examplemod";

    private static boolean showGui = false;
    private static boolean lastFState = false;

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static boolean initialized = false;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 🔥 GLFW F toggle
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (Minecraft.getInstance().player == null) return;

        if (event.getKey() == GLFW.GLFW_KEY_F) {
            boolean pressed = event.getAction() != GLFW.GLFW_RELEASE;
            if (pressed && !lastFState) {
                showGui = !showGui;
            }
            lastFState = pressed;
        }
    }

    // 🔥 вызывается из Mixin КАЖДЫЙ КАДР
    public static void renderImGui() {
        if (!showGui) return;

        if (!initialized) {
            ImGui.createContext();
            imGuiGlfw.init(Minecraft.getInstance().getWindow().getWindow(), false);
            imGuiGl3.init("#version 150");
            initialized = true;
        }

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // ==== IMGUI CONTENT ====
        ImGui.begin("ExampleMod");
        ImGui.text("ImGui работает.");
        ImGui.text("F — toggle");
        if (ImGui.button("Close")) {
            showGui = false;
        }
        ImGui.end();
        // =======================

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
