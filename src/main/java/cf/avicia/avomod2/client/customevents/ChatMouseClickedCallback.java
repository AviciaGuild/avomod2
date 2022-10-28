package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ChatMouseClickedCallback {
    Event<ChatMouseClickedCallback> EVENT = EventFactory.createArrayBacked(ChatMouseClickedCallback.class,
            (listeners) -> (mouseX, mouseY) -> {
                for (ChatMouseClickedCallback listener : listeners) {
                    ActionResult result = listener.mouseClicked(mouseX, mouseY);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult mouseClicked(double mouseX, double mouseY);
}
