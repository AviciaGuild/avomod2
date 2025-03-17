package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class StackDuplicateMessages {

    private static Text lastMessage = null;
    private static ChatHudLine.Visible lastVisibleMessage = null;
    private static int duplicateCount = 1;

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("stackDuplicateMessages")) return message;

        try {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            List<ChatHudLine.Visible> visibleMessages = chatHud.visibleMessages;
            List<ChatHudLine> messages = chatHud.messages;
//            System.out.println(messages);
//            int latestTick = messages.getFirst().creationTick();
            List<ChatHudLine> duplicateMessages = new ArrayList<>();
            List<ChatHudLine.Visible> duplicateVisibleMessages = new ArrayList<>();
            for (ChatHudLine m1 : messages) {
                for (ChatHudLine m2 : messages) {
                    if (!m1.equals(m2) && m1.content().equals(m2.content()) && Math.abs(m1.creationTick() - m2.creationTick()) < 200) {
                        for (ChatHudLine.Visible clv : visibleMessages) {
//                            m2.content().contains(clv.content())
                            int i = MathHelper.floor((double)chatHud.getWidth() / chatHud.getChatScale());
                            MessageIndicator.Icon icon = m2.getIcon();
                            if (icon != null) {
                                i -= icon.width + 4 + 2;
                            }
                            List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(m2.content(), i, MinecraftClient.getInstance().textRenderer);
                            for (OrderedText t : list) {
                                t.accept((index, style, codePoint) -> {
                                    System.out.println("DHJAIWDHAIWDAIWD");
                                    System.out.println(index);
                                    System.out.println(style);
                                    System.out.println(codePoint);
                                    return false;
                                });
                                System.out.println(t.equals(clv.content()));
                            }
                            if (list.contains(clv.content())) {
                                System.out.println("MAYBE");
                                duplicateVisibleMessages.add(clv);

                            }
//                            if (m2.creationTick() == clv.addedTime()) {
////                                System.out.println(cl.content().getString() + " AABNDNMADNADAND");
//                                duplicateVisibleMessages.add(clv);
//                            }
//                    System.out.println(clv.addedTime());
                        }
                        System.out.println("DUPLICATE: " + m1.content().getString());
                        duplicateMessages.add(m2);
                    }
                }
            }
            for (ChatHudLine cl : duplicateMessages) {
                messages.remove(cl);
            }
            for (ChatHudLine.Visible clv : duplicateVisibleMessages) {
                System.out.println(visibleMessages.remove(clv));
            }
//            if (lastMessage != null && lastMessage.equals(message)) {
////                System.out.println(lastMessage);
////                System.out.println(lastVisibleMessage);
////                System.out.println("AAAAAAAAAAAAAAAAA");
//                duplicateCount++;
//                // Visible messages are split per line, not per message, so remove all lines that were created at
//                // the same time as the last message to remove the visible message
//                if (!visibleMessages.isEmpty() && !messages.isEmpty()) {
////                    System.out.println(visibleMessages);
//                    while (!visibleMessages.getFirst().equals(lastVisibleMessage)) {
////                        System.out.println(visibleMessages.getFirst().endOfEntry());
//                        visibleMessages.removeFirst();
//                        if (visibleMessages.isEmpty()) break;
//                    }
//                    if (!messages.isEmpty()) {
//                        messages.removeFirst();
//                    }
//                }
//
//                // If the previous message is successfully deleted add the duplicate count to the new message
//                // This makes it appear as the previous message gets a number added to it, since they have the same text
//                Text tmpMessage = Text.of("");
//                tmpMessage.getSiblings().add(message);
//                tmpMessage.getSiblings().add(Text.of((message.getString().endsWith(" ") ? "" : " ") + String.format("§7(%s)", duplicateCount)));
//                message = tmpMessage;
//            } else {
//                duplicateCount = 1;
//                lastMessage = message;
//            }
//            if (!visibleMessages.isEmpty()) {
//                lastVisibleMessage = visibleMessages.getFirst();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }
}
