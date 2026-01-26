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
import org.lwjgl.glfw.GLFW;

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
        // Инициализация ImGui на клиенте
        long window = Minecraft.getInstance().getWindow().getWindow();
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setIniFilename(null); // Отключаем сохранение ini, если не нужно

        implGlfw.init(window, true);
        implGl3.init(); // Убрал параметр, т.к. в твоей версии ImGui-Java (1.86.10) метод init() без String glslVersion
        initialized = true;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (!initialized) return;

            implGlfw.newFrame();
            ImGui.newFrame();

            // Простой пример GUI: окно с текстом
            ImGui.begin("Пример GUI");
            ImGui.text("Привет из ImGui в Minecraft!");
            ImGui.end();

            ImGui.render();
            implGl3.renderDrawData(ImGui.getDrawData());
        }
    }
}
