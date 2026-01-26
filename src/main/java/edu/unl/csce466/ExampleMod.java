import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

@Mod("examplemod")
public class ExampleMod {

    private final Minecraft mc = Minecraft.getInstance();

    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;
    private boolean initialized = false;
    private boolean showGui = false;

    private final KeyMapping toggleKey = new KeyMapping(
            "key.examplemod.toggle",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            GLFW.GLFW_KEY_F,
            "key.categories.misc"
    );

    public ExampleMod() {
        MinecraftForge.EVENT_BUS.register(this);
        net.minecraftforge.client.ClientRegistry.registerKeyMapping(toggleKey);
    }

    private void initImGui() {
        if (initialized) return;

        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();

        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        imGuiGlfw.init(mc.getWindow().getWindow(), true);
        imGuiGl3.init("#version 150");

        initialized = true;
        System.out.println("[ExampleMod] ImGui initialized OK");
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        if (toggleKey.isDown()) {
            showGui = !showGui;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!showGui) return;

        initImGui();

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.begin("Example Mod Window");
        ImGui.text("Hello, ImGui!");
        ImGui.text("Press F to toggle this window.");
        ImGui.end();

        ImGui.render();
        im
