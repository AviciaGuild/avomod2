package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AuraHandler {
    private static final int auraProcTime = 3200;
    private static final int[] potentialAuraTimes = new int[]{24, 18, 12};

    private static int auraTimer = 0;
    private static long firstAura = 0;
    private static long lastAura = 0;

    private static void auraPinged() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAura > auraProcTime) {
            if (firstAura != 0 && auraTimer == 0) {
                auraTimer = (int) (System.currentTimeMillis() - firstAura) / 1000;

                int[] differences = Arrays.stream(potentialAuraTimes).map(e -> e - auraTimer).toArray();
                int lowestValue = 99999;
                int lowestIndex = 0;
                for (int i = 0; i < differences.length; i++) {
                    if (differences[i] < lowestValue) {
                        lowestIndex = i;
                        lowestValue = differences[i];
                    }
                }

                auraTimer = potentialAuraTimes[lowestIndex];
            }

            firstAura = currentTime;
        }

        lastAura = currentTime;
    }

    public static void render(MatrixStack matrices) {
        if (!ConfigsHandler.getConfigBoolean("auraPing")) return;

        onRenderTick();
        long currentTime = System.currentTimeMillis();

        if (currentTime - firstAura < auraProcTime) {
            long timeRemaining = (auraProcTime - (currentTime - firstAura));
            String remainingTimer = Double.toString(Math.floor(timeRemaining / 100.0) / 10.0);

            matrices.push();
            matrices.scale(6.0F, 6.0F, 6.0F);
            Window window = MinecraftClient.getInstance().getWindow();
            Screen.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of(remainingTimer), window.getScaledWidth() / 12 - MinecraftClient.getInstance().textRenderer.getWidth(remainingTimer) / 3, window.getScaledHeight() / 12 - 10, Color.CYAN.getRGB());
            matrices.pop();

            if (currentTime - firstAura < 400) {
                Color color;
                try {
                    color = Color.decode("#" + ConfigsHandler.getConfig("auraPingColor"));
                } catch (Exception e) {
                    color = new Color(255, 111, 0);
                }
                Screen.fill(matrices, 0, 0, window.getScaledWidth(), window.getScaledHeight(), new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).getRGB());
            }
        }
    }

    public static void resetAura() {
        firstAura = 0;
        auraTimer = 0;
    }

    private static void onRenderTick() {
        try {
            InGameHud inGameHud = MinecraftClient.getInstance().inGameHud;
            Field subtitleField = null;
            for (Field declaredField : inGameHud.getClass().getDeclaredFields()) {
                if (declaredField.getName().equals("subtitle") || declaredField.getName().equals("field_2039")) {
                    subtitleField = declaredField;
                }
            }
            if (subtitleField != null) {
                subtitleField.setAccessible(true);
                Text subtitle = (Text) subtitleField.get(inGameHud);
                if (subtitle == null) return;
                if (subtitle.getString().contains("Aura")) {
                    auraPinged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
