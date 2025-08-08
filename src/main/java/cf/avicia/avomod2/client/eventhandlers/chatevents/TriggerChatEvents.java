package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.commands.subcommands.CongratulateCommand;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.BombBellTracker;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarDPS;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarTracker;
import cf.avicia.avomod2.client.eventhandlers.screenevents.AttackedTerritoryDifficulty;
import net.minecraft.text.Text;

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
        message = CongratulateCommand.onMessage(message);
        if (message == null) {
            return null;
        }
        message = StackDuplicateMessages.onMessage(message);
        if (message == null) {
            return null;
        }
        AttackedTerritoryDifficulty.onMessage(message);
        BombBellTracker.onMessage(message);
        WarDPS.onMessage(message);
        TerritoryTakenWarning.onMessage(message);
        WarTracker.onMessage(message);
        return message;
    }
}
