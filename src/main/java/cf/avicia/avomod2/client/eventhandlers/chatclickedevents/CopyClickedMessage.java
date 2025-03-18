package cf.avicia.avomod2.client.eventhandlers.chatclickedevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class CopyClickedMessage {
    public static ActionResult messageClicked(Text message) {
        if (ConfigsHandler.getConfigBoolean("copyChatMessages") && Utils.isCtrlDown()) {
            MinecraftClient.getInstance().keyboard.setClipboard(Utils.removePrivateUseChars(Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString())));
            return ActionResult.FAIL;
        }
        return ActionResult.SUCCESS;
    }
}
