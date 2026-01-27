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

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return; // Не мешать в меню/инвентаре
        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
        }
    }

    public static void renderImGui() {
        if (!showGui) return;

        if (!initialized) {
            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null);

            imGuiGlfw.init(Minecraft.getInstance().getWindow().getWindow(), true);

            // В 1.90.0 init с параметром работает стабильно
            imGuiGl3.init("#version 150");

            // 🔥 ФИКС АССЕРТА: строим шрифты сразу после init
            io.getFonts().build();

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized successfully!");
        }

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("ExampleMod ImGui GUI");
        ImGui.text("Привет! ImGui работает на 1.19.2");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.separator();
        ImGui.text("Нажми F для скрытия");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
}
