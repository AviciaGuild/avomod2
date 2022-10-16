package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class TriggerHudEvents {
    public static void onRender(MatrixStack matrixStack, float tickDelta) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return;
        WorldInfoOnTab.render(matrixStack);
    }

    public static ActionResult onBossBarRender(MatrixStack matrices, int x, int y, BossBar bossBar) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return ActionResult.SUCCESS;
        List<ActionResult> actionResults = new ArrayList<>();

        actionResults.add(AutoStream.onRenderBossBar(matrices, x, y, bossBar));

        if (actionResults.contains(ActionResult.FAIL)) {
            return ActionResult.FAIL;
        } else {
            return ActionResult.SUCCESS;
        }
    }
}
