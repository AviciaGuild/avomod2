package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public class TimeStampsMixin {
    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"),
            index = 0
    )
    private Text init(Text message) {
        if (ConfigsHandler.getConfigBoolean("chatTimestamps") && !ConfigsHandler.getConfigBoolean("disableAll")) {
            if (!message.getString().matches("§8\\[§7.+§8\\]§r.*")) {  // If there is already a wynntils timestamp don't add a timestamp
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                Text res = Text.of("§8[§7" + timeStamp + "§8]§f ");
                res.getSiblings().add(message);
                return res;
            }
        }
        return message;
    }
}
