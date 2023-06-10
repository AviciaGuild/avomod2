package cf.avicia.avomod2.client.eventhandlers.inventoryclickedevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.screenevents.TradeMarketMiddleClickSearch;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerInventoryMouseClickedEvents {
    public static ActionResult mouseClicked(double mouseX, double mouseY, int button, Slot clickedSlot, ScreenHandler screenHandler) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return ActionResult.SUCCESS;

        List<ActionResult> actionResults = new ArrayList<>();
        actionResults.add(TradeMarketMiddleClickSearch.mouseClicked(mouseX, mouseY, button, clickedSlot, screenHandler));
        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
