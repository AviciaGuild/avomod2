package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;

public class ShowRealName {
    public static ActionResult onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("revealNicks")) return ActionResult.SUCCESS;
        addRealNameToMessage(message);
        return ActionResult.SUCCESS;
    }

    private static void addRealNameToMessage(Text message) {
        if (message.getSiblings().size() > 0) {
            for (Text siblingMessage : message.getSiblings()) {
                addRealNameToMessage(siblingMessage);
            }
        }
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) return;
            if (hover.getValue(hover.getAction()) instanceof Text hoverText) {
                String realName = hoverText.getString().split(" ")[hoverText.getString().split(" ").length - 1];
                // Save all sibling of the message
                List<Text> siblings = message.getSiblings();
                // Make a TextElement with the real name
                Text fullMessage = Text.of("§c(" + realName + ")§f");
                // Add all old siblings to the real name
                fullMessage.getSiblings().addAll(siblings);
                // Clears everything except for the nickname (and [***] stuff if in guild chat)
                message.getSiblings().clear();
                // Adds the real name + the original message after the nickname
                message.getSiblings().add(fullMessage);
            }
//            message.getSiblings().addAll(TextElement.of("§c(" + realName + ")§f").getWithStyle(message.getStyle())); // This is not used due to it appearing after the message in guild chat
        }
    }

    public static boolean messageHasNickHover(Text message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) instanceof Text hoverText) {
            return hoverText.getString().contains("real username");
        }
        return false;
    }
}
