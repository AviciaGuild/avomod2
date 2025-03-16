package cf.avicia.avomod2.client.eventhandlers.chatclickedevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.attacktimermenu.AttackTimerMenu;
import cf.avicia.avomod2.client.eventhandlers.hudevents.BombBellTracker;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerChatMouseClickedEvents {
    public static ActionResult mouseClicked(double mouseX, double mouseY) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return ActionResult.SUCCESS;

        List<ActionResult> actionResults = new ArrayList<>();
        actionResults.add(AttackTimerMenu.mouseClicked(mouseX, mouseY));
        actionResults.add(BombBellTracker.mouseClicked(mouseX, mouseY));
        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
