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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WarTracker {
    private static long lastWarBar;
    private static List<String> members = new ArrayList<>();
    private static List<String> uuids = new ArrayList<>();

    public static void warStart(String territoryName, List<String> members) {
        if (MinecraftClient.getInstance().player != null) {
            String playerUsername = MinecraftClient.getInstance().player.getName().getString();
            List<String> filteredMembers = members.stream().filter(s -> !s.equals(playerUsername)).toList();
            WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
            addWar(currentWarObject);
        }
    }

    public static ElementGroup getElementsToDraw() {
        long weeklyWars = getWars(System.currentTimeMillis() - 604800000L);

        String plural = "";
        if (weeklyWars != 1) {
            plural = "s";
        }

        String text = String.format("%s war%s", weeklyWars, plural);
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
        CustomFile warFile = new CustomFile(ConfigsHandler.getConfigPath("wars"));
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            savedWars.addProperty("wars", "");
        }

        String newWarsString = savedWars.get("wars").getAsString() + warObject + "|";
        savedWars.addProperty("wars", newWarsString);
        warFile.writeJson(savedWars);
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

    public static ActionResult onMessage(Text msg) {
        String message = Utils.getUnformattedString(msg.getString());
        if (message == null) return ActionResult.SUCCESS;

        if (message.startsWith("[WAR] The war battle will start in 25 seconds.")) {
            new Thread(() -> {
                try {
                    members = new ArrayList<>();
                    uuids = new ArrayList<>();
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    int searchRadius = 100;

                    for (int i = 0; i < 25; i++) {
                        List<PlayerEntity> newPlayers = new ArrayList<>();
                        if (player != null && MinecraftClient.getInstance().world != null) {
                            newPlayers = MinecraftClient.getInstance().world.getEntitiesByClass(PlayerEntity.class, new Box(
                                    player.getX() - searchRadius, player.getY() - searchRadius, player.getZ() - searchRadius,
                                    player.getX() + searchRadius, player.getY() + searchRadius, player.getZ() + searchRadius
                            ), Objects::nonNull);
                        }
                        List<String> newMembers = new ArrayList<>(newPlayers.stream().map(e -> e.getName().getString()).toList());
                        List<String> newUuids = new ArrayList<>(newPlayers.stream().map(Entity::getUuidAsString).toList());
                        newMembers.removeAll(members);
                        members.addAll(newMembers);

                        newUuids.removeAll(uuids);
                        uuids.addAll(newUuids);

                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return ActionResult.SUCCESS;
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

    public static void render(MatrixStack matrices) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return;

        if (ConfigsHandler.getConfigBoolean("displayWeeklyWarcount")) {
            WarTracker.getElementsToDraw().draw(matrices);
        }
    }
}
