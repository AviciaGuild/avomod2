package cf.avicia.avomod2.client.configs.locations;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.core.CustomFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;


public class LocationsHandler {

    public static JsonObject locations = null;

    public static Map<String, String> defaultLocations = new HashMap<>() {{
        put("weeklyWars", "1,0.98,false");
        put("worldInfoOnTab", "1,0.7,false");
        put("attacksMenu", "1,0.1,false");
        put("tabStatusDisplay", "0.4,.1,true");
        put("warDPS", "0,0.2,true");
        put("bombBellTracker", "1,0.25,false");
    }};

    public static void initializeLocations() {

        CustomFile locationsFile = new CustomFile(ConfigsHandler.getConfigPath("locations"));
        JsonObject locationsJson = locationsFile.readJson();
        boolean locationsChanged = false;

        for (Map.Entry<String, String> locationData : LocationsHandler.defaultLocations.entrySet()) {
            JsonElement locationsElement = locationsJson.get(locationData.getKey());

            if (locationsElement == null || locationsElement.isJsonNull()) {
                locationsJson.addProperty(locationData.getKey(), locationData.getValue());
                locationsChanged = true;
            }
        }

        if (locationsChanged) {
            locationsFile.writeJson(locationsJson);
        }
        LocationsHandler.locations = locationsFile.readJson();
    }

    public static void save(ElementGroup multipleElements) {
        CustomFile locationsFile = new CustomFile(ConfigsHandler.getConfigPath("locations"));
        JsonObject locations = locationsFile.readJson();
        locations.addProperty(multipleElements.getKey(), multipleElements.toString());

        LocationsHandler.locations = locations;
        locationsFile.writeJson(locations);
    }

    public static String getLocation(String locationKey) {
        JsonElement locationElement = locations.get(locationKey);

        if (locationElement == null || locationElement.isJsonNull()) {
            return defaultLocations.get(locationKey);
        } else {
            return locationElement.getAsString();
        }
    }

    public static float getStartX(String key, int rectangleWidth, float scale) {
        String locationText = getLocation(key);
        if (locationText == null) return (float) 0;

        float screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth() / scale;
        float x = (Float.parseFloat(locationText.split(",")[0]) * screenWidth);
        boolean alignment = Boolean.parseBoolean(locationText.split(",")[2]);

        return alignment ? x : x - rectangleWidth;
    }

    public static float getStartY(String key, float scale) {
        String locationText = getLocation(key);
        if (locationText == null) return (float) 0;

        float screenHeight = (MinecraftClient.getInstance().getWindow().getScaledHeight() / scale);
        return Float.parseFloat(locationText.split(",")[1]) * screenHeight;
    }
}
