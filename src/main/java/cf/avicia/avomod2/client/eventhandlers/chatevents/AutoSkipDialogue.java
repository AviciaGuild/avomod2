package cf.avicia.avomod2.client.eventhandlers.chatevents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class AutoSkipDialogue {
    public static ActionResult onMessage(Text message) {
        KeyBinding sneakKeyBind = MinecraftClient.getInstance().options.sneakKey;
        if ((message.getString().contains("                       Press SHIFT to continue") || message.getString().contains("                       Press SNEAK to continue"))) {
            Thread thread = new Thread(() -> {
                try {
                    sneakKeyBind.setPressed(true);
                    Thread.sleep(100);
                    sneakKeyBind.setPressed(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        return ActionResult.SUCCESS;
    }
}
