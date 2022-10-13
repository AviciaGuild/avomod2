package cf.avicia.avomod2.client.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class ConfigsHandler {
    private void initializeConfigs() {
        CustomFile configsFile = new CustomFile(getConfigPath("configs"));
        JsonObject configsJson = configsFile.readJson();
        boolean configsChanged = false;

        for (Config config : configsArray) {
            JsonElement configElement = configsJson.get(config.configsKey);

            if (configElement == null || configElement.isJsonNull()) {
                configsJson.addProperty(config.configsKey, config.defaultValue);
                configsChanged = true;
            }
        }

        if (configsChanged) {
            configsFile.writeJson(configsJson);
        }
        Avomod.configs = configsJson;

        CustomFile locationsFile = new CustomFile(getConfigPath("locations"));
        JsonObject locationsJson = locationsFile.readJson();
        boolean locationsChanged = false;

        for (Map.Entry<String, String> locationData : defaultLocations.entrySet()) {
            JsonElement locationsElement = locationsJson.get(locationData.getKey());

            if (locationsElement == null || locationsElement.isJsonNull()) {
                locationsJson.addProperty(locationData.getKey(), locationData.getValue());
                locationsChanged = true;
            }
        }

        if (locationsChanged) {
            locationsFile.writeJson(locationsJson);
        }
        Avomod.locations = locationsFile.readJson();
    }

    private void initializeAliases() {
        for (Map.Entry<String, Command> commandMap : commands.entrySet()) {
            Command command = commandMap.getValue();

            for (String alias : command.getAliases()) {
                aliases.put(alias, command);
            }
        }
    }

    public static String getConfigPath(String name) {
        return String.format("avomod/%s/%s.json", Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", ""), name);
    }
}
