package cf.avicia.avomod2.inventoryoverlay.util;

import cf.avicia.avomod2.inventoryoverlay.item.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ItemsDataHandler {
    public static Map<String, WynnItem> items;
    public static Map<String, Set<String>> possibleFilters = new HashMap<>();
    private static boolean isCurrentlyFetchingItemData = false;

    public static void updateItemsFromAPI() {
        updateItemsFromAPI(stringWynnItemMap -> {
        });
    }

    public static void updateItemsFromAPI(Consumer<Map<String, WynnItem>> callback) {
        if (isCurrentlyFetchingItemData) {
            return;
        }
        isCurrentlyFetchingItemData = true;
        new Thread(() -> {
            try {
                String allItemDataString = WebRequest.getData("https://api.wynncraft.com/v3/item/database?fullResult");
                items = parseAPIStringToItems(allItemDataString);
                callback.accept(items);
                if (items == null) {
                    items = parseAPIStringToItems(ConfigFileUtil.readFile("all-wynncraft-items-backup.json"));
                } else {
                    ConfigFileUtil.writeFile("all-wynncraft-items-backup.json", allItemDataString);
                }
                isCurrentlyFetchingItemData = false;
            } catch (Exception e) {
                items = parseAPIStringToItems(ConfigFileUtil.readFile("all-wynncraft-items-backup.json"));
                e.printStackTrace();
            }
        }).start();
    }

    private static Map<String, WynnItem> parseAPIStringToItems(String apiString) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, WynnItem>>() {
                }.getType(), new WynnItemDeserializer())
                .registerTypeAdapter(new TypeToken<Map<String, Identification>>() {
                }.getType(), new IdentificationDeserializer())
                .registerTypeAdapter(DroppedBy.class, new DroppedByDeserializer())
                .create();
        Type type = new TypeToken<Map<String, WynnItem>>() {
        }.getType();
        return gson.fromJson(apiString, type);
    }
}
