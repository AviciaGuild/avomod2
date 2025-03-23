package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.utils.Utils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class MakeShoutsClickable {

    public static Text onMessage(Text message) {
        try {
            makeShoutClickable((MutableText) message);
        } catch (Exception ignored) {}
        return message;
    }

    private static void makeShoutClickable(MutableText message) {
        if (isMessageShout(message)) {
            try {
                String messageString = Utils.getUnformattedString(message.getString());
                String command = "/msg " + messageString.substring(0, messageString.indexOf("[") - 1) + " ";
                message.fillStyle(message.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isMessageShout(Text message) {
        Pattern pattern = Pattern.compile("^(.* \\[[A-Z]{2}\\d*] shouts:) .*", Pattern.CASE_INSENSITIVE);
        String messageString =  Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString());
        return pattern.matcher(messageString).find();
    }
}
