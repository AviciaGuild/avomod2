package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

public interface RenderItemCallback {
    Event<RenderItemCallback> EVENT = EventFactory.createArrayBacked(RenderItemCallback.class,
            (listeners) -> (itemStack) -> {
                for (RenderItemCallback listener : listeners) {
                    listener.onRenderItem(itemStack);
                }
            });

    void onRenderItem(ItemStack itemStack);
}
