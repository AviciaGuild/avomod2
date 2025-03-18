package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ChatMessageClickedCallback {
    Event<ChatMessageClickedCallback> EVENT = EventFactory.createArrayBacked(ChatMessageClickedCallback.class,
            (listeners) -> (message) -> {
                for (ChatMessageClickedCallback listener : listeners) {
                    ActionResult result = listener.messageClicked(message);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult messageClicked(Text message);
}
