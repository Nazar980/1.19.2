package edu.unl.csce466.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import edu.unl.csce466.ExampleMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)  // Отключаем remap для всего класса
public class RenderSystemMixin {
    @Inject(method = "flipFrame(J)V", at = @At("HEAD"), remap = false)  // Повторно отключаем remap
    private static void onFlipFrame(long window, CallbackInfo ci) {
        ExampleMod.renderImGui();
    }
}
