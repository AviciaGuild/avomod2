package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface ChatRenderCallback {
    Event<ChatRenderCallback> EVENT = EventFactory.createArrayBacked(ChatRenderCallback.class,
            (listeners) -> (context, currentTick, mouseX, mouseY, focused) -> {
                for (ChatRenderCallback listener : listeners) {
                    listener.render(context, currentTick, mouseX, mouseY, focused);
                }
            });

    void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused);
}
