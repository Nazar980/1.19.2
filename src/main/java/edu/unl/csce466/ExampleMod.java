package edu.unl.csce466;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod(ExampleMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ExampleMod {
    public static final String MODID = "examplemod";

    private static boolean showGui = false;

    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private static boolean initialized = false;

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Toggle на F
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
            showGui = !showGui;
            System.out.println("[ExampleMod] GUI toggled: " + showGui);
        }
    }

    // Рендер ImGui каждый кадр после HUD
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // Только после основного HUD (minecraft:all)
        if (!event.getOverlay().id().getNamespace().equals("minecraft") ||
            !event.getOverlay().id().getPath().equals("all")) {
            return;
        }

        if (!showGui) return;

        // Защита от другого потока
        if (!Thread.currentThread().getName().equals("Render thread")) {
            System.err.println("[ImGui] Wrong thread: " + Thread.currentThread().getName());
            return;
        }

        if (!initialized) {
            System.out.println("[ExampleMod] Initializing ImGui...");

            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.setIniFilename(null);

            long window = Minecraft.getInstance().getWindow().getWindow();
            imGuiGlfw.init(window, true);
            imGuiGl3.init("#version 150");

            // Фикс assertion
            io.getFonts().build();

            initialized = true;
            System.out.println("[ExampleMod] ImGui initialized OK!");
        }

        // Сохраняем GL-состояние Minecraft
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableTexture();

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("Simple ImGui Test");
        ImGui.text("Привет! Если ты это видишь — всё работает");
        ImGui.text("Версия ImGui: " + ImGui.getVersion());
        ImGui.text("F — toggle");
        if (ImGui.button("Закрыть")) {
            showGui = false;
        }
        ImGui.end();

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // Восстанавливаем состояние
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popMatrix();
    }
}
