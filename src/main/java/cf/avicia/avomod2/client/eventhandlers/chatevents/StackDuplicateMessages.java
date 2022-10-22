package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.lang.reflect.Field;
import java.util.List;

public class StackDuplicateMessages {

    private static Text lastMessage = null;
    private static int duplicateCount = 1;

    public static ActionResult onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("stackDuplicateMessages")) return ActionResult.SUCCESS;
        if (lastMessage != null && lastMessage.equals(message)) {
            duplicateCount++;
            try {
                // Tries to remove the previous message
                ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
                Field visibleMessagesField = null;
                for (Field declaredField : chatHud.getClass().getSuperclass().getDeclaredFields()) {
                    // If chat overlay such as wynntils is used, check the parent
                    if (declaredField.getName().equals("visibleMessages") || declaredField.getName().equals("field_2064")) {
                        visibleMessagesField = declaredField;
                    }
                }
                for (Field declaredField : chatHud.getClass().getDeclaredFields()) {
                    // Also check regular chatHud
                    if (declaredField.getName().equals("visibleMessages") || declaredField.getName().equals("field_2064")) {
                        visibleMessagesField = declaredField;
                    }
                }
                if (visibleMessagesField != null) {
                    visibleMessagesField.setAccessible(true);
                    List<ChatHudLine<OrderedText>> visibleMessages = (List<ChatHudLine<OrderedText>>) visibleMessagesField.get(chatHud);
                    visibleMessages.remove(0);
                    // If the previous message is successfully deleted add the duplicate count to the new message
                    // This makes it appear as the previous message gets a number added to it, since they have the same text
                    message.getSiblings().add(Text.of(String.format("§7(%s)", duplicateCount)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            duplicateCount = 1;
            lastMessage = message;
        }
        return ActionResult.SUCCESS;
    }
}
