package cf.avicia.avomod2;

import cf.avicia.avomod2.client.configs.Config;
import cf.avicia.avomod2.client.configs.ConfigInput;
import cf.avicia.avomod2.client.configs.ConfigToggle;
import net.fabricmc.api.ModInitializer;

public class AvoMod2 implements ModInitializer {
    public static Config[] configsArray = new Config[]{
            new ConfigToggle("General", "Disable Everything", "Disabled", "disableAll"),
            new ConfigToggle("General", "Notify for avomod BETA Version (may have bugs)", "Disabled", "betaNotification"),
            new ConfigToggle("Guild", "Filter Out Bank Messages", "Disabled", "filterBankMessages"),
            new ConfigToggle("Guild", "Filter Out All Resource Messages", "Disabled", "filterResourceMessages"),
            new ConfigToggle("Chat", "Reveal Nicknames", "Enabled", "revealNicks"),
            new ConfigToggle("Chat", "Auto Skip Quest Dialogue", "Disabled", "skipDialogue"),
            new ConfigToggle("Chat", "Click to Say Congrats Message", "Enabled", "clickToSayCongrats"),
            new ConfigInput("Chat", "Click to say congrats message", "Congrats!", ".+", "^.+$", 30, "congratsMessage"),
            new ConfigToggle("War", "Custom Attack Timers Display", "Enabled", "attacksMenu"),
            new ConfigToggle("War", "Green Beacon at Soonest War", "Enabled", "greenBeacon"),
            new ConfigToggle("War", "Announce Territory Defense in Chat", "Enabled", "terrDefenseInChat"),
            new ConfigToggle("War", "Display War Info (dps, tower ehp, etc.)", "Enabled", "dpsInWars"),
            new ConfigToggle("War", "Hide Entities in Wars", "Disabled", "hideEntitiesInWar"),
            new ConfigToggle("War", "Aura Ping", "Enabled", "auraPing"),
            new ConfigInput("War", "Aura Ping Color", "FF6F00", "[\\da-fA-F]+", "^[\\da-fA-F]{6}$", 6, "auraPingColor"),
            new ConfigToggle("War", "Display Weekly Warcount on Screen", "Disabled", "displayWeeklyWarcount"),
            new ConfigToggle("War", "Prevent joining wars when afk", "Enabled", "afkWarProtection"),
            new ConfigInput("War", "Minutes until considered afk", "10", "[0-9]+", "^[0-9]+$", 3, "afkTime"),
            new ConfigInput("War", "Territory attack confirmation threshold", "15000", "[0-9]+", "^[0-9]+$", 6, "attackConfirmation"),
            new ConfigToggle("War", "Send defenses from attacked territories to server (improves accuracy of timer list for guild members)", "Enabled", "storeDefs"),
            new ConfigToggle("Misc", "Auto /stream on World Swap", "Disabled", "autoStream"),
            new ConfigToggle("Misc", "Prevent Moving Armor/Accessories", "Disabled", "disableMovingArmor"),
            new ConfigToggle("Misc", "Make Mob Health Bars More Readable", "Enabled", "readableHealth"),
            new ConfigToggle("Misc", "Display Some Tab Stats on Screen", "Disabled", "tabStatusDisplay"),
            new ConfigToggle("Misc", "Bomb Bell Tracker (REQUIRES CHAMPION)", "Enabled", "bombBellTracker"),
            new ConfigToggle("Misc", "Bomb Bell Tracker - Click to Switch World", "Enabled", "bombBellSwitchWorld")
    };
    @Override
    public void onInitialize() {

    }
}
