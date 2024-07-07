package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.core.CustomFile;
import cf.avicia.avomod2.utils.TerritoryData;
import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.utils.WarObject;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class WarTracker {
    private static long lastWarBar;
    private static HashSet<String> members = new HashSet<>();
    private static long timeOf25SecondMessage = 0;
    private static final List<WarObject> weeklyWars = loadWeeklyWars();

    public static void warStart(String territoryName, HashSet<String> members) {
        if (MinecraftClient.getInstance().player != null) {
            String playerUsername = MinecraftClient.getInstance().player.getName().getString();
            List<String> filteredMembers = members.stream().filter(s -> !s.equals(playerUsername)).toList();
            WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
            addWar(currentWarObject);
        }
    }

    public static ElementGroup getElementsToDraw() {
        long totalWeeklyWars = weeklyWars.size();

        String plural = "";
        if (totalWeeklyWars != 1) {
            plural = "s";
        }

        String text = String.format("%s war%s", totalWeeklyWars, plural);
        int rectangleWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + 4;
        int rectangleHeight = 12;
        float scale = 1.5F;

        float x = LocationsHandler.getStartX("weeklyWars", rectangleWidth, scale);
        float y = LocationsHandler.getStartY("weeklyWars", scale);

        RectangleElement newRectangleElement = new RectangleElement(x, y,
                rectangleWidth, rectangleHeight, scale, new Color(100, 100, 100, 100));
        TextElement newTextElement = new TextElement(text, x + 2, y + 2, scale, Color.MAGENTA);

        return new ElementGroup("weeklyWars", scale, Arrays.asList(newRectangleElement, newTextElement));
    }

    public static void addWar(WarObject warObject) {
        weeklyWars.add(warObject);

        CustomFile warFile = new CustomFile(ConfigsHandler.getConfigPath("wars"));
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            savedWars.addProperty("wars", "");
        }

        String newWarsString = savedWars.get("wars").getAsString() + warObject + "|";
        savedWars.addProperty("wars", newWarsString);
        warFile.writeJson(savedWars);
    }

    private static List<WarObject> loadWeeklyWars() {
        CustomFile warFile = new CustomFile(ConfigsHandler.getConfigPath("wars"));
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            return new ArrayList<>();
        }

        String wars = savedWars.get("wars").getAsString();

        long currentMills = System.currentTimeMillis() - 604800000L;

        return Arrays.stream(wars.split("\\|"))
                .map(WarObject::parseString)
                .filter(war -> war.getWarStart() > currentMills)
                .collect(Collectors.toList());
    }

    public static long getWars(long timeSince) {
        CustomFile warFile = new CustomFile(ConfigsHandler.getConfigPath("wars"));
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            return 0;
        }

        String wars = savedWars.get("wars").getAsString();
        List<String> warsAfter = Arrays.stream(wars.split("\\|")).filter(e -> Long.parseLong(e.split("/")[2]) > timeSince).toList();
        return warsAfter.size();
    }

    public static long timeOfFirstWar() {
        CustomFile warFile = new CustomFile(ConfigsHandler.getConfigPath("wars"));
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            return 0;
        }

        String[] wars = savedWars.get("wars").getAsString().split("\\|");
        if (wars.length == 0) return 0;

        return Long.parseLong(wars[0].split("/")[2]);
    }

    public static Text onMessage(Text msg) {
        String message = Utils.getUnformattedString(msg.getString());
        if (message == null) return msg;

        if (message.startsWith("[WAR] The war battle will start in 25 seconds.")) {
            timeOf25SecondMessage = System.currentTimeMillis();
            members = new HashSet<>();
        }
        return msg;
    }

    private static int entityTickCounter = 0;
    public static void afterEntityRender() {
        entityTickCounter++;
        if (System.currentTimeMillis() - timeOf25SecondMessage > 25_000) return;
        if (entityTickCounter % 20 != 0) return;
        entityTickCounter = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int searchRadius = 100;

        List<PlayerEntity> newPlayers = new ArrayList<>();
        if (player != null && MinecraftClient.getInstance().world != null) {
            newPlayers = MinecraftClient.getInstance().world.getEntitiesByClass(PlayerEntity.class, new Box(
                    player.getX() - searchRadius, player.getY() - searchRadius, player.getZ() - searchRadius,
                    player.getX() + searchRadius, player.getY() + searchRadius, player.getZ() + searchRadius
            ), playerEntity -> playerEntity != null && playerEntity.getName().getString().matches("[A-Za-z_0-9]+"));
        }
        List<String> newMembers = new ArrayList<>(newPlayers.stream().map(e -> e.getName().getString()).toList());
        members.addAll(newMembers);
    }

    public static ActionResult onRenderBossBar(BossBar bossBar) {
        try {
            String bossBarName = Utils.getUnformattedString(bossBar.getName().getString());
            if (bossBarName == null) return ActionResult.SUCCESS;

            if (bossBarName.contains("Tower")) {
                if (System.currentTimeMillis() - lastWarBar < 25000) {
                    lastWarBar = System.currentTimeMillis();
                    return ActionResult.SUCCESS;
                }
                lastWarBar = System.currentTimeMillis();

                String[] territorySplit = bossBarName.split(" - ")[0].split("] ");
                if (territorySplit.length == 1) return ActionResult.SUCCESS;
                String[] territoryWords = territorySplit[1].split(" ");

                String territoryName = String.join(" ", Arrays.copyOfRange(territoryWords, 0, territoryWords.length - 1));

                if (TerritoryData.territoryList != null && TerritoryData.territoryList.contains(territoryName)) {
                    WarTracker.warStart(territoryName, members);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ActionResult.SUCCESS;
    }

    public static void render(DrawContext drawContext) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return;

        if (ConfigsHandler.getConfigBoolean("displayWeeklyWarcount")) {
            WarTracker.getElementsToDraw().draw(drawContext);
        }
    }

    public static void onTick() {
        long currentMills = System.currentTimeMillis() - 604800000L;

        weeklyWars.removeIf(war -> war.getWarStart() < currentMills);
    }
}
