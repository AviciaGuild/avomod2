package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class FilterMessages {
    public static Text onMessage(Text message) {
        List<Text> actionResults = new ArrayList<>();

        actionResults.add(filterWelcomeMessage(message));
        actionResults.add(filterGuildBankMessage(message));
        actionResults.add(filterResourceMessage(message));

        if (actionResults.contains(null)) {
            return null;
        } else {
            return message;
        }
    }

    private static Text filterWelcomeMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterWelcomeMessage")) {
            if (message.getString().contains("  §6§lWelcome to Wynncraft!") ||
                    Utils.textWithoutTimeStamp(message).getString().equals("Your class has automatically been selected. Use /class to change your class, or /toggle autojoin to turn this feature off.")) {
                return null;
            }
        }
        return message;
    }

    private static Text filterGuildBankMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterBankMessages")) {
            if (Utils.textWithoutTimeStamp(message).getString().startsWith("§3[INFO§3]") && message.getString().contains("Guild Bank")) {
                return null;
            }
        }
        return message;
    }
    private static Text filterResourceMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterResourceMessages")) {
            if (Utils.textWithoutTimeStamp(message).getString().startsWith("§3[INFO§3]") && message.getString().contains("resources")) {
                return null;
            }
        }
        return message;
    }

}
