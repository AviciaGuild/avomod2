package cf.avicia.avomod2.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public class TimeStampsMixin {
    @ModifyArg(method = "addMessage(Lnet/minecraft/text/Text;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"),
            index = 0)
    private Text init(Text message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Text res = Text.of( "§8[§7" + timeStamp + "§8]§f ");
        res.getSiblings().add(message);
        return res;
    }
}
