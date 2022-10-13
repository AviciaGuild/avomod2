package cf.avicia.avomod2.client.eventhandlers.chatevents;

import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerChatEvents {
    public static ActionResult trigger(LiteralText message) {
        List<ActionResult> actionResults = new ArrayList<>();
        actionResults.add(ShowRealName.onMessage(message));
        actionResults.add(MakeShoutsClickable.onMessage(message));
        actionResults.add(AutoSkipDialogue.onMessage(message));
        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
