package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    private static final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    public ExampleMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        long window = Minecraft.getInstance().getWindow().getWindow();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setIniFilename(null);

        implGlfw.init(window, true);

        // В 1.90+ часто работает без параметра
        // Если краш — попробуй закомментировать и раскомментировать строку ниж
        implGl3.init("#version 150");
  // основной вариант для 1.90+

        // Альтернатива, если первый крашит:
        // implGl3.init("#version 130");  // или 150, 330 core — пробуй по очереди

        initialized = true;
        System.out.println("[ExampleMod] ImGui 1.90 initialized (backend OK)");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (!initialized) return;

            implGlfw.newFrame();
            ImGui.newFrame();

            ImGui.begin("ImGui 1.90 Test");
            ImGui.text("Братан, если это видно — backend работает!");
            ImGui.text("Версия: " + ImGui.getVersion());
            ImGui.end();

            ImGui.render();
            implGl3.renderDrawData(ImGui.getDrawData());
        }
    }
}
