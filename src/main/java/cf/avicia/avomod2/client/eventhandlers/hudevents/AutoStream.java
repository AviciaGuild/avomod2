package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;

public class AutoStream {

    private static long lastObservedStreamerMode = 0;
    private static Thread thread = null;

    public static ActionResult onRenderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
        if (ConfigsHandler.getConfigBoolean("autoStream")) {
            String bossBarText = bossBar.getName().getString();
            if (bossBarText != null) {
                if (bossBarText.contains("Streamer mode enabled")) {
                    lastObservedStreamerMode = System.currentTimeMillis();
                } else {
                    // Only enter streamer mode if not in hunted and not having been in streamer mode in the last second
                    if (MinecraftClient.getInstance().player != null && !Utils.inHuntedMode() && System.currentTimeMillis() - lastObservedStreamerMode > 1500) {
                        if (thread == null || !thread.isAlive()) {
                            thread = new Thread(() -> {
                                // Makes sure to wait at least one second between /stream command, to prevent it from spamming
                                // This means streamer mode has time to activate before the command runs again, so it doesn't run again until streamer mode is disabled
                                try {
                                    MinecraftClient.getInstance().player.sendChatMessage("/stream");
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            thread.start();
                        }
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}
