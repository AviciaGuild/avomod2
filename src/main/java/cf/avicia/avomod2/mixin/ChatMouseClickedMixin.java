package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatMessageClickedCallback;
import cf.avicia.avomod2.client.customevents.ChatMouseClickedCallback;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatMouseClickedMixin {
    @Inject(method = "mouseClicked(DD)Z",
            at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        try {
            List<ActionResult> results = new ArrayList<>();
            Text clickedMessage = Utils.getChatMessageAt(mouseX, mouseY);
            if (clickedMessage != null) {
                results.add(ChatMessageClickedCallback.EVENT.invoker().messageClicked(clickedMessage));
            }
            results.add(ChatMouseClickedCallback.EVENT.invoker().mouseClicked(mouseX, mouseY));
            if (results.contains(ActionResult.FAIL)) {
                // Canceling this doesn't seem to actually do anything, but I'll keep it in for now
                cir.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}