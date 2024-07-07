package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatMixin {
    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"),
            index = 0
//            cancellable = true
    )
    private Text onMessage(Text message) {
        try {
            message = ChatMessageCallback.EVENT.invoker().onMessage(message);
            if (message == null) {
                return Text.of("Message filtered");
//                ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
