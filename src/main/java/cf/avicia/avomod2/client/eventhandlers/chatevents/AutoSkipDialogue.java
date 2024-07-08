package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class AutoSkipDialogue {
    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("skipDialogue")) return message;
        KeyBinding sneakKeyBind = MinecraftClient.getInstance().options.sneakKey;
        if ((message.getString().contains("  Press SHIFT to continue") || message.getString().contains("  Press SNEAK to continue"))) {
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
        return message;
    }
}
