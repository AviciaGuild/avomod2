package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;

public class StackDuplicateMessages {

    private static Text lastMessage = null;
    private static ChatHudLine.Visible lastVisibleMessage = null;
    private static int duplicateCount = 1;

    public static ActionResult onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("stackDuplicateMessages")) return ActionResult.SUCCESS;

        try {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            List<ChatHudLine.Visible> visibleMessages = chatHud.visibleMessages;
            List<ChatHudLine> messages = chatHud.messages;
            if (lastMessage != null && lastMessage.equals(message)) {
                duplicateCount++;
                // Visible messages are split per line, not per message, so remove all lines that were created at
                // the same time as the last message to remove the visible message
                if (visibleMessages.size() > 0) {
                    while (!visibleMessages.get(0).equals(lastVisibleMessage)) {
                        visibleMessages.remove(0);
                        if (visibleMessages.size() == 0) break;
                    }
                    messages.remove(0);
                }

                // If the previous message is successfully deleted add the duplicate count to the new message
                // This makes it appear as the previous message gets a number added to it, since they have the same text
                message.getSiblings().add(Text.of((message.getString().endsWith(" ") ? "" : " ") + String.format("§7(%s)", duplicateCount)));
            } else {
                duplicateCount = 1;
                lastMessage = message;
            }
            if (visibleMessages.size() > 0) {
                lastVisibleMessage = visibleMessages.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ActionResult.SUCCESS;
    }
}
