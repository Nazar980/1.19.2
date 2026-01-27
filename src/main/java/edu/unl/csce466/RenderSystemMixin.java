package edu.unl.csce466.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import edu.unl.csce466.ExampleMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    // Фикс: SRG имя метода flipFrame в 1.19.2 official mappings — m_109147_(J)V
    @Inject(method = "m_109147_(J)V", at = @At("HEAD"))
    private static void onFlipFrame(long window, CallbackInfo ci) {
        ExampleMod.renderImGui();
    }
}
