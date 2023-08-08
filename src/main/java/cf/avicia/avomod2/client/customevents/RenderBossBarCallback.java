package cf.avicia.avomod2.client.customevents;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;

/**
 * Runs when a boss bar is being rendered
 * Cancellable by returning ActionResult.FAIL (hides the boss bar)
 */
public interface RenderBossBarCallback {
    Event<RenderBossBarCallback> EVENT = EventFactory.createArrayBacked(RenderBossBarCallback.class,
            (listeners) -> (matrices, x, y, bossBar) -> {
                for (RenderBossBarCallback listener : listeners) {
                    ActionResult result = listener.onRender(matrices, x, y, bossBar);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult onRender(DrawContext drawContext, int x, int y, BossBar bossBar);
}
