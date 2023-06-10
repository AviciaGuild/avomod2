package cf.avicia.avomod2.utils;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TerritoryData {
    private final static Map<String, String> defenses = new HashMap<>();
    public static List<String> territoryList;
    private static JsonObject territoryData;
    private static boolean hasDataBeenRequested = false;

    private static int tick = 0;

    public static void updateTerritoryData() {
        try {
            if (MinecraftClient.getInstance().getNetworkHandler() == null) return;

            for (Advancement advancement : MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler().getManager().getAdvancements()) {
                if (advancement.getDisplay() != null) {
                    try {
                        String territoryName = advancement.getDisplay().getTitle().getString().trim();
                        String territoryDataFormatted = Utils.getUnformattedString(advancement.getDisplay().getDescription().getString());
                        if (territoryDataFormatted == null) return;

                        String territoryData = territoryDataFormatted.trim().replaceAll("\\s+", " ").replaceAll("\\n", " ");

                        int index1 = territoryData.indexOf("Territory Defences: ");
                        int index2 = territoryData.indexOf(" Trading Routes");

                        if (index1 != -1 && index2 != -1) {
                            String territoryDefense = territoryData.substring(index1 + 20, index2);

                            defenses.put(territoryName, territoryDefense);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Pair<String, Long> getTerritoryDefense(String territoryName, Long warTimestamp) {
        return new Pair<>(defenses.getOrDefault(territoryName, "Unknown"), warTimestamp);
    }

    public static String territoryAtCoordinates(Pair<Integer, Integer> coordinates) {
        if (territoryData == null) return null;
        for (Map.Entry<String, JsonElement> territory : territoryData.getAsJsonObject("territories").entrySet()) {
            JsonObject locationObject = territory.getValue().getAsJsonObject().getAsJsonObject("location");
            int startX = Math.min(locationObject.get("startX").getAsInt(), locationObject.get("endX").getAsInt());
            int startY = Math.min(locationObject.get("startY").getAsInt(), locationObject.get("endY").getAsInt());
            int endX = Math.max(locationObject.get("startX").getAsInt(), locationObject.get("endX").getAsInt());
            int endY = Math.max(locationObject.get("startY").getAsInt(), locationObject.get("endY").getAsInt());

            if (coordinates.getA() > startX && coordinates.getA() < endX && coordinates.getB() > startY && coordinates.getB() < endY) {
                return territory.getKey();
            }
        }

        return null;
    }

    private static void makeApiRequest() {
        hasDataBeenRequested = true;
        try {
            new Thread(() -> {
                String response = WebRequest.getData("https://api.wynncraft.com/public_api.php?action=territoryList");
                territoryData = new Gson().fromJson(response, JsonObject.class);
                territoryList = territoryData.getAsJsonObject("territories").entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            }).start();
        } catch (Exception e) {
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    makeApiRequest();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }).start();
        }

    }

    public static Coordinates getMiddleOfTerritory(String territory) {
        if (territoryData == null) return null;
        if (!territoryData.has("territories")) return null;
        JsonObject territoryObject = territoryData.getAsJsonObject("territories").getAsJsonObject(territory);
        if (territoryObject == null) return null;

        JsonObject locationObject = territoryObject.getAsJsonObject("location");
        if (locationObject.isJsonNull()) return null;

        int middleX = (locationObject.get("startX").getAsInt() + locationObject.get("endX").getAsInt()) / 2;
        int middleZ = (locationObject.get("startY").getAsInt() + locationObject.get("endY").getAsInt()) / 2;
        return new Coordinates(middleX, 0, middleZ);
    }

    public static void onTick() {
        if (ConfigsHandler.getConfigBoolean("disableAll") || MinecraftClient.getInstance().player == null) return;
        if (!hasDataBeenRequested) {
            makeApiRequest();
        }
        tick++;
        if (tick >= 10000 || defenses.isEmpty()) {
            updateTerritoryData();
            tick = 0;
        }
    }
}
