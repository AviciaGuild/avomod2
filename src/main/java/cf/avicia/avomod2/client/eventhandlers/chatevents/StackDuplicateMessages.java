package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;

public class StackDuplicateMessages {

    private static Text lastMessage = null;
    private static int duplicateCount = 1;

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("stackDuplicateMessages")) return message;

        try {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            List<ChatHudLine.Visible> visibleMessages = chatHud.visibleMessages;
            List<ChatHudLine> messages = chatHud.messages;
            if (messages.isEmpty()) {
                return message;
            }
            if (!message.equals(lastMessage)) {
                duplicateCount = 1;
            }
            lastMessage = message;
            if (Utils.getChatMessageWithOnlyMessage(messages.getFirst().content()).equals(Utils.getChatMessageWithOnlyMessage(message))) {
                for (int i : Utils.getVisibleMessagesByMessageIndex(0)) {
                    visibleMessages.remove(i);
                }
                messages.removeFirst();
                duplicateCount++;
                // If the previous message is successfully deleted add the duplicate count to the new message
                // This makes it appear as the previous message gets a number added to it, since they have the same text
                Text tmpMessage = Text.of("");
                tmpMessage.getSiblings().add(message);
                tmpMessage.getSiblings().add(Text.of((message.getString().endsWith(" ") ? "" : " ") + String.format("§7(%s)", duplicateCount)));
                message = tmpMessage;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}
