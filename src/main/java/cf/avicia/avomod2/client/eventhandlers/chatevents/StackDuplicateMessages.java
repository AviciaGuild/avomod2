package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.MessageType;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;

public class StackDuplicateMessages {

    private static int duplicateCount = 1;
    private static MessageType lastMessageType = null;

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("stackDuplicateMessages")) return message;
        try {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            List<ChatHudLine.Visible> visibleMessages = chatHud.visibleMessages;
            List<ChatHudLine> messages = chatHud.messages;
            if (messages.isEmpty()) {
                return message;
            }
            MessageType messageType = Utils.getMessageType(message);
            if (messageType != lastMessageType) {
                duplicateCount = 1;
                lastMessageType = messageType;
                return message;
            }

            String mostRecentMessage = Utils.getChatMessageWithOnlyMessage(messages.getFirst().content());
            String newMessage = Utils.getChatMessageWithOnlyMessage(message);
            if (newMessage.equals(mostRecentMessage) && MinecraftClient.getInstance().inGameHud.getTicks() == messages.getFirst().creationTick()) {
                // Allow duplicates created at the exact same time to not break Wynntils chat tabs.
                // The chat tabs call ChatHud's addMessage twice at the same time for it to appear in two tabs,
                // for this mod however they look like distinct messages, so I make the assumption here that two
                // identical messages created at the exact same time are a byproduct of chat tabs and don't filter them.
                return message;
            }
            if (mostRecentMessage.equals(newMessage)) {
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
            } else {
                duplicateCount = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}
