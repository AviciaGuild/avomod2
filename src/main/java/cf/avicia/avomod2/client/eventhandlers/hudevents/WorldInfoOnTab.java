package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.Utils;
import cf.avicia.avomod2.webrequests.aviciaapi.UpTimes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class WorldInfoOnTab {

    private static UpTimes upTimes = null;

    public static void updateUpTimes() {
        Thread thread = new Thread(() -> {
            try {
                upTimes = new UpTimes();
                // Re-runs the update function every 5 minutes, to keep up with new worlds being started.
                // The current world's age is abased on a timestamp, so we don't need to update for its sake
                Thread.sleep(60 * 5 * 1000);
                updateUpTimes();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        if (!thread.isAlive()) {
            thread.start();
        }
    }
    public static void render(MatrixStack matrixStack) {
        if (upTimes != null && MinecraftClient.getInstance().options.playerListKey.isPressed()) {
            int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            if (Utils.getCurrentWorld() != null) {
                Text yourWorldText = Text.of("Your world §b" + Utils.getCurrentWorld() + "§f: " + Utils.getReadableTime(upTimes.getAge(Utils.getCurrentWorld())));
                Screen.drawTextWithShadow(matrixStack, textRenderer, yourWorldText, screenWidth - textRenderer.getWidth(yourWorldText), screenHeight * 3/5, 0xffffff);
            }
            if (upTimes.getNewestWorld() != null) {
                Text newestWorldText = Text.of("Newest world §b" + upTimes.getNewestWorld() + "§f: " + Utils.getReadableTime(upTimes.getAge(upTimes.getNewestWorld())));
                Screen.drawTextWithShadow(matrixStack, textRenderer, newestWorldText, screenWidth - textRenderer.getWidth(newestWorldText), screenHeight * 3/5 + 10, 0xffffff);
            }
        }
    }
}
