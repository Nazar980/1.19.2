package edu.unl.csce466.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import edu.unl.csce466.ExampleMod;
import imgui.ImGui;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

    @Inject(method = "flipFrame(J)V", at = @At("HEAD"))
    private static void onFlipFrame(long window, CallbackInfo ci) {
        ExampleMod.renderImGui();
    }
}
