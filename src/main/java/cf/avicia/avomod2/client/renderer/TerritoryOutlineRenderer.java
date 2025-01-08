package cf.avicia.avomod2.client.renderer;

import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.utils.TerritoryData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TerritoryOutlineRenderer {
    private static KeyBinding keyBinding;
    private static boolean showOutline = false;

    public static void initKeybind() {
        keyBinding = new KeyBinding("Toggle territory highlights", InputUtil.GLFW_KEY_COMMA, "Avomod");
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    public static void onTick() {
        if (MinecraftClient.getInstance().player == null) return;

        if (keyBinding.wasPressed()) {
            showOutline = !showOutline;
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(showOutline ? "§7Territory outlines §aEnabled" : "§7Territory outlines §cDisabled"));
        }
    }

    public static void renderOutline(WorldRenderContext context) {
        if (!showOutline) {
            return;
        }
        if (MinecraftClient.getInstance().player != null && context.matrixStack() != null && TerritoryData.territoryData != null) {
            Camera camera = context.camera();
            Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
            for (Map.Entry<String, JsonElement> territory : TerritoryData.territoryData.entrySet()) {
                JsonObject locationObject = territory.getValue().getAsJsonObject().getAsJsonObject("location");
                int apiStartX = locationObject.get("start").getAsJsonArray().get(0).getAsInt();
                int apiStartZ = locationObject.get("start").getAsJsonArray().get(1).getAsInt();
                int apiEndX = locationObject.get("end").getAsJsonArray().get(0).getAsInt();
                int apiEndZ = locationObject.get("end").getAsJsonArray().get(1).getAsInt();
                int startX = Math.min(apiStartX, apiEndX);
                int startZ = Math.min(apiStartZ, apiEndZ);
                int endX = Math.max(apiStartX, apiEndX);
                int endZ = Math.max(apiStartZ, apiEndZ);

                double dist = Math.min(Math.abs(playerPos.x - startX), Math.abs(playerPos.x - endX)) + Math.min(Math.abs(playerPos.z - startZ), Math.abs(playerPos.z - endZ));
                int maxDistance = MinecraftClient.getInstance().options.getClampedViewDistance() * 15;
                if (dist > maxDistance) {
                    continue;
                }
                int xPos = MinecraftClient.getInstance().player.getBlockPos().getX();
                int zPos = MinecraftClient.getInstance().player.getBlockPos().getZ();
                String currentTerritory = TerritoryData.territoryAtCoordinates(new Pair<>(xPos, zPos));
                boolean inTerr = currentTerritory != null && currentTerritory.equals(territory.getKey());

                for (int level = -20; level < 20; level++) {
                    DebugRenderer.drawBox(
                            context.matrixStack(),
                            context.consumers(),
                            startX - camera.getPos().x,
                            16 * level - camera.getPos().y,
                            startZ - camera.getPos().z,
                            endX - camera.getPos().x,
                            16 * level - camera.getPos().y,
                            endZ - camera.getPos().z,
                            inTerr ? 0 : 1,
                            inTerr ? 1 : 0,
                            0,
                            .5f
                    );
                }

            }
        }
    }

    public static void renderText(DrawContext drawContext) {
        getElementsToDraw().draw(drawContext);
    }

    public static ElementGroup getElementsToDraw() {
        List<Element> elementsList = new ArrayList<>();
        float scale = 1F;
        if (!showOutline) {
            return new ElementGroup("territoryName", scale, elementsList);
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int rectangleHeight = 12;
        float startY = LocationsHandler.getStartY("territoryName", scale);
        if (MinecraftClient.getInstance().player != null) {
            int xPos = MinecraftClient.getInstance().player.getBlockPos().getX();
            int zPos = MinecraftClient.getInstance().player.getBlockPos().getZ();
            String currentTerritory = TerritoryData.territoryAtCoordinates(new Pair<>(xPos, zPos));
            if (currentTerritory != null) {
                int rectangleWidth = textRenderer.getWidth(currentTerritory) + 4;
                float yourWorldStartX = LocationsHandler.getStartX("territoryName", rectangleWidth, scale);
                elementsList.add(new RectangleElement(yourWorldStartX, startY + rectangleHeight, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
                elementsList.add(new TextElement(currentTerritory, yourWorldStartX + 2, startY + 2 + rectangleHeight, scale, Color.WHITE));
            }
        }

        return new ElementGroup("territoryName", scale, elementsList);
    }

    public static ElementGroup getElementsToDraw(String placeholder) {
        List<Element> elementsList = new ArrayList<>();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int rectangleHeight = 12;
        float scale = 1F;
        float startY = LocationsHandler.getStartY("territoryName", scale);
        int rectangleWidth = textRenderer.getWidth(placeholder) + 4;
        float yourWorldStartX = LocationsHandler.getStartX("territoryName", rectangleWidth, scale);
        elementsList.add(new RectangleElement(yourWorldStartX, startY + rectangleHeight, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
        elementsList.add(new TextElement(placeholder, yourWorldStartX + 2, startY + 2 + rectangleHeight, scale, Color.WHITE));


        return new ElementGroup("territoryName", scale, elementsList);
    }
}
