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

    // ImGui backend
    private static final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();
    private static boolean initialized = false;

    // ❌ Никакого ImGui в конструкторе и clientSetup

    @Mod.EventBusSubscriber(
            modid = MODID,
            bus = Mod.EventBusSubscriber.Bus.FORGE,
            value = Dist.CLIENT
    )
    public static class ClientEvents {

        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            // ✅ Инициализация один раз, когда GL контекст уже активен
            if (!initialized) {
                long window = Minecraft.getInstance().getWindow().getWindow();

                ImGui.createContext();
                ImGuiIO io = ImGui.getIO();
                io.setIniFilename(null);

                implGlfw.init(window, true);
                implGl3.init("#version 150");

                // 🔑 Важно: добавить шрифт и собрать
                io.getFonts().addFontDefault();
                io.getFonts().build();

                initialized = true;
                System.out.println("[ExampleMod] ImGui initialized OK");
            }

            // ✅ Каждый кадр
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
