package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.Utils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.regex.Pattern;

public class MakeShoutsClickable {

    public static ActionResult onMessage(LiteralText message) {
        makeShoutClickable(message);
        return ActionResult.SUCCESS;
    }

    private static void makeShoutClickable(LiteralText message) {
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
        Pattern pattern = Pattern.compile("^(.* \\[WC\\d*] shouts:) .*", Pattern.CASE_INSENSITIVE);
        String messageString =  Utils.getUnformattedString(message.getString());
        return pattern.matcher(messageString).find();
    }
}
