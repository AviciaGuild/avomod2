package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.utils.MessageType;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeShoutsClickable {

    public static Text onMessage(Text message) {
        try {
            makeShoutClickable((MutableText) message);
        } catch (Exception ignored) {}
        return message;
    }

    private static void makeShoutClickable(MutableText message) {
        if (Utils.getMessageType(message) == MessageType.SHOUT) {
            try {
                String username = "";
                if (ShowRealName.messageHasNickHoverDeep(message)) {
                    username = ShowRealName.getRealName(message);
                } else {
                    String regex = "(?<username>[^ ]+) shouts: .*";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(Utils.getChatMessageWithOnlyMessage(message));
                    if (matcher.find()) {
                        username = matcher.group("username");
                    }
                }
                String command = "/msg " + username + " ";
                message.fillStyle(message.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
