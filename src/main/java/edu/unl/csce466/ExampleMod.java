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
        // Инициализация ImGui на клиенте (только один раз)
        long window = Minecraft.getInstance().getWindow().getWindow();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Включаем клавиатуру (опционально)
        io.setIniFilename(null); // Отключаем сохранение настроек в файл

        // GLFW backend
        implGlfw.init(window, true);

        // OpenGL3 backend — без параметра, т.к. в 1.90+ init() без args использует дефолтный GLSL
        // Если метод возвращает boolean — проверяем успех
        boolean gl3Success = implGl3.init();  // ← основной фикс: принимаем boolean

        if (!gl3Success) {
            System.err.println("[ImGui Mod] Failed to initialize ImGui OpenGL3 backend!");
            // Можно добавить: ImGui.destroyContext(); или просто отключить рендер
            return;
        }

        initialized = true;
        System.out.println("[ImGui Mod] ImGui initialized successfully!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (!initialized) return;

            // Начинаем новый кадр ImGui
            implGlfw.newFrame();
            ImGui.newFrame();

            // Простое тестовое окно
            ImGui.begin("Пример GUI от ImGui в Minecraft");
            ImGui.text("Привет! Это работает :)");
            ImGui.text("Версия ImGui: " + ImGui.getVersion());
            ImGui.separator();
            if (ImGui.button("Закрыть")) {
                // Можно добавить логику выхода или скрытия
            }
            ImGui.end();

            // Финализируем кадр и рендерим
            ImGui.render();
            implGl3.renderDrawData(ImGui.getDrawData());
        }
    }
}
