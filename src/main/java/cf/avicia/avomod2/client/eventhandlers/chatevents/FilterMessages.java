package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class FilterMessages {
    public static ActionResult onMessage(Text message) {
        List<ActionResult> actionResults = new ArrayList<>();

        actionResults.add(filterWelcomeMessage(message));
        actionResults.add(filterGuildBankMessage(message));
        actionResults.add(filterResourceMessage(message));

        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }    }

    private static ActionResult filterWelcomeMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterWelcomeMessage")) {
            if (message.getString().contains("  §6§lWelcome to Wynncraft!") ||
                    message.getString().equals("Your class has automatically been selected. Use /class to change your class, or /toggle autojoin to turn this feature off.")) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.SUCCESS;
    }

    private static ActionResult filterGuildBankMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterBankMessages")) {
            if (message.getString().startsWith("§3[INFO§3]") && message.getString().contains("Guild Bank")) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.SUCCESS;
    }
    private static ActionResult filterResourceMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("filterResourceMessages")) {
            if (message.getString().startsWith("§3[INFO§3]") && message.getString().contains("resources")) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.SUCCESS;
    }

}
