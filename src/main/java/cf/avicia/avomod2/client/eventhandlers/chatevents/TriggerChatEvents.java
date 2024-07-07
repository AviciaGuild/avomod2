package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.commands.subcommands.CongratulateCommand;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.BombBellTracker;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarDPS;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarTracker;
import cf.avicia.avomod2.client.eventhandlers.screenevents.AttackedTerritoryDifficulty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerChatEvents {
    public static Text onMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return message;


        message = ShowRealName.onMessage(message);
        if (message == null) {
            return null;
        }
        MakeShoutsClickable.onMessage(message);
        AutoSkipDialogue.onMessage(message);
        message = FilterMessages.onMessage(message);
        if (message == null) {
            return null;
        }
        CongratulateCommand.onMessage(message);
        message = StackDuplicateMessages.onMessage(message);
        if (message == null) {
            return null;
        }
        AttackedTerritoryDifficulty.onMessage(message);
        BombBellTracker.onMessage(message);
        WarDPS.onMessage(message);
        WarTracker.onMessage(message);
        return message;
    }
}
