package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

/**
 * Runs when a chat message is received (before being displayed)
 * Cancellable by returning ActionResult.FAIL
 */
public interface ChatMessageCallback {
    Event<ChatMessageCallback> EVENT = EventFactory.createArrayBacked(ChatMessageCallback.class,
            (listeners) -> (message) -> {
                for (ChatMessageCallback listener : listeners) {
                    ActionResult result = listener.onMessage(message);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult onMessage(Text message);
}
