package cf.avicia.avomod2.client.eventhandlers.chatclickedevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class CopyClickedMessage {


    public static ActionResult messageClicked(Text message) {
        if (ConfigsHandler.getConfigBoolean("copyChatMessages") && Utils.isCtrlDown()) {
            MinecraftClient.getInstance().keyboard.setClipboard(Utils.getChatMessageWithOnlyMessage(message));
            return ActionResult.FAIL;
        }
        return ActionResult.SUCCESS;
    }

    public static void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused) {
        if (ConfigsHandler.getConfigBoolean("copyChatMessages") && Utils.isCtrlDown()) {
            Text hoveredMessage = Utils.getChatMessageAt(mouseX, mouseY);
            if (hoveredMessage != null) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of(MinecraftClient.getInstance().keyboard.getClipboard().equals(Utils.getChatMessageWithOnlyMessage(hoveredMessage)) ? "Copied to clipboard!" : "Click to copy message"), mouseX, mouseY);
            }
        }
    }
}
