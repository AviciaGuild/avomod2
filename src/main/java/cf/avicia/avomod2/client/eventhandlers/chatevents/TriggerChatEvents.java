package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.commands.subcommands.CongratulateCommand;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.BombBellTracker;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarDPS;
import cf.avicia.avomod2.client.eventhandlers.screenevents.AttackedTerritoryDifficulty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerChatEvents {
    public static ActionResult onMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return ActionResult.SUCCESS;

        List<ActionResult> actionResults = new ArrayList<>();
        actionResults.add(ShowRealName.onMessage(message));
        actionResults.add(MakeShoutsClickable.onMessage(message));
        actionResults.add(AutoSkipDialogue.onMessage(message));
        actionResults.add(FilterMessages.onMessage(message));
        actionResults.add(CongratulateCommand.onMessage(message));
        actionResults.add(StackDuplicateMessages.onMessage(message));
        actionResults.add(AttackedTerritoryDifficulty.onMessage(message));
        actionResults.add(BombBellTracker.onMessage(message));
        actionResults.add(WarDPS.onMessage(message));
        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
