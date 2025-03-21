package cf.avicia.avomod2.client.configs;

import cf.avicia.avomod2.core.CustomFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

public class ConfigsHandler {

    public static JsonObject configs = null;
    private static CustomFile configsFile = null;


    public static Config[] configsArray = new Config[]{
            new ConfigToggle("General", "Disable Everything", "Disabled", "disableAll"),
            new ConfigInput("General", "Avicia api domain (default: www.avicia.info)", "www.avicia.info", ".+", "^.+$", 80, "aviciaApiDomain"),
            new ConfigToggle("General", "Share advancements data to avo map", "Enabled", "shareAdvancementsData"),
            new ConfigToggle("Guild", "Filter Out Bank Messages", "Disabled", "filterBankMessages"),
            new ConfigToggle("Guild", "Filter Out All Resource Messages", "Disabled", "filterResourceMessages"),
            new ConfigToggle("Chat", "Reveal Nicknames", "Enabled", "revealNicks"),
            new ConfigToggle("Chat", "Auto Skip Quest Dialogue", "Disabled", "skipDialogue"),
            new ConfigToggle("Chat", "Show Chat Timestamps", "Enabled", "chatTimestamps"),
            new ConfigToggle("Chat", "Stack Duplicate Messages", "Enabled", "stackDuplicateMessages"),
            new ConfigToggle("Chat", "Filter Out Welcome Message", "Disabled", "filterWelcomeMessage"),
            new ConfigToggle("Chat", "Click to Say Congrats Message", "Enabled", "clickToSayCongrats"),
            new ConfigInput("Chat", "Click to say congrats message", "Congrats!", ".+", "^.+$", 30, "congratsMessage"),
            new ConfigToggle("War", "Custom Attack Timers Display", "Enabled", "attacksMenu"),
            new ConfigToggle("War", "Green Beacon at Soonest War", "Enabled", "greenBeacon"),
            new ConfigToggle("War", "Announce Territory Defense in Chat", "Enabled", "terrDefenseInChat"),
            new ConfigToggle("War", "Display War Info (dps, tower ehp, etc.)", "Enabled", "dpsInWars"),
            new ConfigToggle("War", "Aura Ping", "Enabled", "auraPing"),
            new ConfigInput("War", "Aura Ping Color", "FF6F00", "[\\da-fA-F]+", "^[\\da-fA-F]{6}$", 6, "auraPingColor"),
            new ConfigToggle("War", "Display Weekly Warcount on Screen", "Disabled", "displayWeeklyWarcount"),
            new ConfigToggle("War", "Play a sound when a territory gets taken", "Disabled", "territoryTakenSound"),
            new ConfigToggle("Misc", "Auto /stream on World Swap", "Disabled", "autoStream"),
            new ConfigToggle("Misc", "Add item overlay and search bar to inventories", "Enabled", "itemOverlay"),
            new ConfigToggle("Misc", "Hide item overlay and show only search bar", "Disabled", "onlySearchBar"),
            new ConfigToggle("Misc", "Add profession highlighter to containers", "Enabled", "profHighlighter"),
            new ConfigToggle("Misc", "Copy chat messages by ctrl clicking them", "Enabled", "copyChatMessages"),
            new ConfigToggle("Misc", "Make Mob Health Bars More Readable", "Enabled", "readableHealth"),
            new ConfigToggle("Misc", "Bomb Bell Tracker (REQUIRES CHAMPION)", "Enabled", "bombBellTracker"),
            new ConfigToggle("Misc", "Bomb Bell Tracker - Click to Switch World", "Enabled", "bombBellSwitchWorld"),
            new ConfigToggle("Locations", "Edit AvoMod Locations", "Edit", "locations")
    };
    public static void initializeConfigs() {
        configsFile = new CustomFile(getConfigPath("configs"));
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
        configs = configsJson;
    }

    public static String getConfigPath(String name) {
        return String.format("avomod/%s/%s.json", MinecraftClient.getInstance().getSession().getUuidOrNull().toString().replaceAll("-", ""), name);
    }

    public static String getConfig(String configKey) {
        if (configs == null) {
            return "";
        }
        JsonElement configElement = configs.get(configKey);

        if (configElement == null || configElement.isJsonNull()) {
            return "";
        } else {
            return configElement.getAsString();
        }
    }

    public static void updateConfigs(String configsKey, String newValue) {
        JsonObject configsJson = configsFile.readJson();
        configsJson.addProperty(configsKey, newValue);

        if (configsKey.equals("autoStream") && newValue.equals("Disabled")) {
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendCommand("stream");
            }
        }

        ConfigsHandler.configs = configsJson;
        configsFile.writeJson(configsJson);
    }

    public static boolean getConfigBoolean(String configKey) {
        String configValue = getConfig(configKey);

        return configValue.equals("Enabled");
    }
}
