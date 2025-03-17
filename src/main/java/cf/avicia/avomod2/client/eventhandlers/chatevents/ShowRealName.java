package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class ShowRealName {
    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("revealNicks")) return message;
        return addRealNameToMessage(message);
    }

    private static Text addRealNameToMessage(Text message) {
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
                    Text realNameMessage = addRealNameToMessage(siblingMessage);
                    realNameMessage = tryToAddRealName(realNameMessage);
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

    private static Text tryToAddRealName(Text message) {
        if (messageHasNickHover(message)) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            if (hover == null) return message;
            if (hover.getValue(hover.getAction()) instanceof Text hoverText) {
                String realName = hoverText.getString().split(" ")[hoverText.getString().split(" ").length - 1];
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
            if (hover.getValue(hover.getAction()) instanceof Text hoverText) {
                return hoverText.getString().split(" ")[hoverText.getString().split(" ").length - 1];
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
        if (hover != null && hover.getValue(hover.getAction()) instanceof Text hoverText) {
            return hoverText.getString().contains("real username");
        }
        return false;
    }
}
