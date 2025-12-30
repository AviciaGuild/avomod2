package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class ShowRealName {
    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("revealNicks")) return message;
        return addRealNameToMessage(message, message);
    }

    private static Text addRealNameToMessage(Text message, Text originalMessage) {
        // No need to do anything if a nickname can't be found in the message
        if (!messageHasNickHoverDeep(message)) {
            return message;
        }
        Text finalMessage = Text.empty().fillStyle(message.getStyle());
        if (!message.getSiblings().isEmpty()) {
            for (Text siblingMessage : message.getSiblings()) {
                // Don't mess with components without nicknames, to minimize potential errors
                if (messageHasNickHoverDeep(siblingMessage)) {
                    // Chat messages can be deeply nested, so we need to use recursion
                    Text realNameMessage = addRealNameToMessage(siblingMessage, originalMessage);
                    realNameMessage = tryToAddRealName(realNameMessage, originalMessage);
                    finalMessage.getSiblings().addAll(realNameMessage.getWithStyle(message.getStyle()));
                } else {
                    finalMessage.getSiblings().addAll(siblingMessage.getWithStyle(siblingMessage.getStyle()));
                }
            }
        } else {
            return message;
        }
        return finalMessage;
    }

    private static Text tryToAddRealName(Text message, Text originalMessage) {
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) return message;
            if (hover instanceof HoverEvent.ShowText(Text value)) {
                String realName = value.getString().split(" ")[value.getString().split(" ").length - 1];
                if (originalMessage.getString().contains("§c(" + realName + ")§f")) {
                    // The real name has already been added (some sort of mod incompatibility)
                    return message;
                }
                Text fullMessage = Text.empty().fillStyle(message.getStyle());
                // Retain the old style
                fullMessage.getSiblings().addAll(message.getWithStyle(message.getStyle()));
                fullMessage.getSiblings().add(Text.of("§c(" + realName + ")§f"));
                return fullMessage;
            }
        }
        return message;
    }

    public static String getRealName(Text message) {
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) return null;
            if (hover instanceof HoverEvent.ShowText(Text value)) {
                return value.getString().split(" ")[value.getString().split(" ").length - 1];
            }
        }
        if (!message.getSiblings().isEmpty()) {
            for (Text messageSibling : message.getSiblings()) {
                String realName = getRealName(messageSibling);
                if (realName != null) {
                    return realName;
                }
            }
        }
        return null;
    }

    public static boolean messageHasNickHoverDeep(Text message) {
        boolean hasNick = false;
        if (!message.getSiblings().isEmpty()) {
            for (Text messageSibling : message.getSiblings()) {
                hasNick = hasNick || messageHasNickHoverDeep(messageSibling);
            }
        } else {
            return messageHasNickHover(message);
        }
        return hasNick;
    }
    public static boolean messageHasNickHover(Text message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover instanceof HoverEvent.ShowText(Text value)) {
            return value.getString().contains("real username") || value.getString().contains("real name");
        }
        return false;
    }
}
