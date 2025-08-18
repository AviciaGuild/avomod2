package cf.avicia.avomod2.client.emotes;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.core.CustomFile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnlockedEmotesDetector {

    private static final Set<String> detectedEmotes = new HashSet<>();
    private static Set<String> lastSavedEmotes = new HashSet<>();

    public static String extractEmoteName(String input) {
        Pattern pattern = Pattern.compile("^([A-Za-z0-9_]+) Emote$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            return matcher.group(1).toLowerCase();
        } else {
            return null;
        }
    }
    public static void afterRender(MinecraftClient client, Screen screen, Screen screen1) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || client.player == null || screen == null) return;
        if (!(screen1 instanceof GenericContainerScreen) || !screen1.getTitle().getString().equals("\uDAFF\uDFF8\uE033\uDAFF\uDF80\uF016")) // Emotes in silly wynncraft text
            return;
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        for (Slot slot : screenHandler.slots) {
            if (slot.getStack().isOf(Items.AIR) || slot.inventory.equals(client.player.getInventory())) {
                continue;
            }
            String emoteName = extractEmoteName(slot.getStack().getName().getString());
            if (emoteName != null) {
                detectedEmotes.add(emoteName);
            }
        }
        if (!lastSavedEmotes.equals(detectedEmotes)) {
            lastSavedEmotes = new HashSet<>(detectedEmotes);
            saveSettings();
        }
    }

    private static void saveSettings() {
        CustomFile customFile = new CustomFile(ConfigsHandler.getConfigPath("emotes"));
        JsonObject savedEmotes = customFile.readJson();

        if (!savedEmotes.has("unlocked")) {
            savedEmotes.add("unlocked", new JsonArray());
        }

        JsonArray newUnlocked = new JsonArray();

        for (String emote : detectedEmotes) {
            newUnlocked.add(emote);
        }

        savedEmotes.add("unlocked", newUnlocked);

        customFile.writeJson(savedEmotes);
    }
}
