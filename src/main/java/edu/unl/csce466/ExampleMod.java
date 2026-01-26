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

        // Фикс: просто вызываем без присваивания (возвращает void в твоей версии)
        implGl3.init();  // или implGl3.init("#version 150"); если хочешь явно

        initialized = true;
        System.out.println("[ExampleMod] ImGui initialized!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (!initialized) return;

            implGlfw.newFrame();
            ImGui.newFrame();

            ImGui.begin("Пример GUI от ImGui");
            ImGui.text("Привет! Это работает :)");
            ImGui.text("Версия ImGui: " + ImGui.getVersion());
            if (ImGui.button("Тест кнопка")) {
                System.out.println("Кнопка нажата!");
            }
            ImGui.end();

            ImGui.render();
            implGl3.renderDrawData(ImGui.getDrawData());
        }
    }
}
