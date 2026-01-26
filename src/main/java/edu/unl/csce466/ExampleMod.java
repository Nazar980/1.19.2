package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ExampleMod.MODID)
public class ExampleMod {

    public static final String MODID = "examplemod";

    private static final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    // ❌ НИКАКОГО ImGui в конструкторе и clientSetup

    @Mod.EventBusSubscriber(
            modid = MODID,
            bus = Mod.EventBusSubscriber.Bus.FORGE,
            value = Dist.CLIENT
    )
    public static class ClientEvents {

        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {

            // ✅ ИНИЦИАЛИЗАЦИЯ (ОДИН РАЗ, КОГДА GL КОНТЕКСТ УЖЕ ЕСТЬ)
            if (!initialized) {
                long window = Minecraft.getInstance().getWindow().getWindow();

                ImGui.createContext();
                ImGuiIO io = ImGui.getIO();
                io.setIniFilename(null);

                implGlfw.init(window, true);

                // 🔑 КЛЮЧЕВОЕ МЕСТО — теперь OpenGL context активен
                implGl3.init("#version 150");

                initialized = true;
                System.out.println("[ExampleMod] ImGui initialized OK");
            }

            // ✅ КАЖДЫЙ КАДР
            implGlfw.newFrame();
            ImGui.newFrame();

            ImGui.begin("ImGui Test");
            ImGui.text("Если ты это видишь — всё работает 😎");
            ImGui.text("ImGui version: " + ImGui.getVersion());
            ImGui.end();

            ImGui.render();
            implGl3.renderDrawData(ImGui.getDrawData());
        }
    }
}
