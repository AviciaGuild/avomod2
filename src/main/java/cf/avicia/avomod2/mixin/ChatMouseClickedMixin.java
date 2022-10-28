package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatMouseClickedCallback;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public class ChatMouseClickedMixin {
    @Inject(method = "mouseClicked(DD)Z",
            at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        try {
            ActionResult result = ChatMouseClickedCallback.EVENT.invoker().mouseClicked(mouseX, mouseY);
            if (result == ActionResult.FAIL) {
                cir.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}