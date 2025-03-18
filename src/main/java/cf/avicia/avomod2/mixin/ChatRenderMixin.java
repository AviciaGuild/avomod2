package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)

public class ChatRenderMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V",
            at = @At("HEAD"))
    public void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        ChatRenderCallback.EVENT.invoker().render(context, currentTick, mouseX, mouseY, focused);
    }

}
