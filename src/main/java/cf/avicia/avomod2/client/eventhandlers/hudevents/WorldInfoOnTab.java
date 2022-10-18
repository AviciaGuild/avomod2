package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.Utils;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.webrequests.aviciaapi.UpTimes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WorldInfoOnTab {

    private static UpTimes upTimes = null;

    public static void updateUpTimes() {
        Thread thread = new Thread(() -> {
            try {
                upTimes = new UpTimes();
                // Re-runs the update function every 5 minutes, to keep up with new worlds being started.
                // The current world's age is based on a timestamp, so we don't need to update for its sake
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
            getElementsToDraw().draw(matrixStack);
        }
    }

    public static ElementGroup getElementsToDraw() {
        List<Element> elementsList = new ArrayList<>();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int rectangleHeight = 12;
        float scale = 1F;
        float startY = LocationsHandler.getStartY("worldInfoOnTab", scale);
        if (Utils.getCurrentWorld() != null) {
            String yourWorldText = "Your world §b" + Utils.getCurrentWorld() + "§f: " + Utils.getReadableTime(upTimes.getAge(Utils.getCurrentWorld()));
            int rectangleWidth = textRenderer.getWidth(yourWorldText) + 4;
            float yourWorldStartX = LocationsHandler.getStartX("worldInfoOnTab", rectangleWidth, scale);
            elementsList.add(new RectangleElement(yourWorldStartX, startY + rectangleHeight, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
            elementsList.add(new TextElement(yourWorldText, yourWorldStartX + 2, startY + 2 + rectangleHeight, scale, Color.WHITE));
        }
        if (upTimes.getNewestWorld() != null) {
            String newestWorldText = "Newest world §b" + upTimes.getNewestWorld() + "§f: " + Utils.getReadableTime(upTimes.getAge(upTimes.getNewestWorld()));
            int rectangleWidth = textRenderer.getWidth(newestWorldText) + 4;
            float newestWorldStartX = LocationsHandler.getStartX("worldInfoOnTab", rectangleWidth, scale);
            elementsList.add(new RectangleElement(newestWorldStartX, startY, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
            elementsList.add(new TextElement(newestWorldText, newestWorldStartX + 2, startY + 2, scale, Color.WHITE));
        }
        return new ElementGroup("worldInfoOnTab", scale, elementsList);
    }
}
