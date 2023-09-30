package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerHudEvents {
    public static void onRender(DrawContext drawContext, float tickDelta) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return;
        WorldInfoOnTab.render(drawContext);
        AttackTimerMenu.render(drawContext);
        WarTracker.render(drawContext);
        BombBellTracker.render(drawContext);
        AuraHandler.render(drawContext);
        WarDPS.render(drawContext);
    }

    public static ActionResult onBossBarRender(DrawContext drawContext, int x, int y, BossBar bossBar) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return ActionResult.SUCCESS;
        List<ActionResult> actionResults = new ArrayList<>();

        actionResults.add(AutoStream.onRenderBossBar(drawContext, x, y, bossBar));
        actionResults.add(ReadableMobHealth.onRenderBossBar(drawContext, x, y, bossBar));
        actionResults.add(WarDPS.onRenderBossBar(drawContext, x, y, bossBar));
        actionResults.add(WarTracker.onRenderBossBar(bossBar));

        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
