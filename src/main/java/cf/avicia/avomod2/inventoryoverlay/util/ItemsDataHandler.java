package cf.avicia.avomod2.inventoryoverlay.util;

import cf.avicia.avomod2.inventoryoverlay.item.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ItemsDataHandler {
    public static String itemFetchProgress = "";
    public static Map<String, WynnItem> items;
    public static Map<String, Set<String>> possibleFilters = new HashMap<>();
    public static boolean isCurrentlyFetchingItemData = false;

    public static void updateItemsFromAPI() {
        updateItemsFromAPI(stringWynnItemMap -> {
        });
    }

    public static void updateItemsFromAPI(Consumer<Map<String, WynnItem>> callback) {
        if (isCurrentlyFetchingItemData) {
            return;
        }
        loadFromBackup(callback); // If we have items backed up, use those and load new data in the background
        isCurrentlyFetchingItemData = true;
        new Thread(() -> {
            try {
                final int MAX_REQUESTS_PER_MINUTE = 50;
                final long MIN_DELAY_MS = 60000 / MAX_REQUESTS_PER_MINUTE;

                final int MAX_RETRIES_PER_PAGE = 10;

                Gson gson = new Gson();
                Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

                Map<String, Object> allItemsMap = new HashMap<>();

                int currentPage = 1;
                int totalPages = 1;

                while (currentPage <= totalPages) {

                    int attempts = 0;
                    boolean success = false;

                    while (!success && attempts < MAX_RETRIES_PER_PAGE) {
                        long requestStart = System.currentTimeMillis();

                        try {
                            String url = "https://api.wynncraft.com/v3/item/database?page=" + currentPage;
                            String response = WebRequest.getData(url);

                            if (response == null || response.isEmpty() || !response.trim().startsWith("{")) {
                                throw new RuntimeException("Invalid/non-JSON response");
                            }

                            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                            JsonObject controller = json.getAsJsonObject("controller");
                            JsonObject results = json.getAsJsonObject("results");

                            if (controller == null || results == null) {
                                throw new RuntimeException("Missing controller/results");
                            }

                            totalPages = controller.get("pages").getAsInt();

                            Map<String, Object> pageItems = gson.fromJson(results, mapType);
                            if (pageItems != null) {
                                allItemsMap.putAll(pageItems);
                            }

                            int percent = (int) ((currentPage * 100.0) / totalPages);

                            itemFetchProgress = "Loading from API... " + percent + "% (" +
                                    currentPage + "/" + totalPages +
                                    ", total items: " + allItemsMap.size() + ")";

                            success = true;

                            long elapsed = System.currentTimeMillis() - requestStart;
                            Thread.sleep(Math.max(MIN_DELAY_MS - elapsed, 0));

                        } catch (Exception e) {
                            attempts++;

                            long waitTime = 2000L * attempts; // grows: 2s, 4s, 6s...
                            System.out.println("InventoryOverlay: Page " + currentPage + " failed (attempt " + attempts + "): "
                                    + e.getMessage() + " → waiting " + waitTime + "ms");

                            Thread.sleep(waitTime);
                        }
                    }

                    if (!success) {
                        throw new RuntimeException("Failed to fetch page " + currentPage + " after many retries");
                    }

                    currentPage++;
                }

                if (allItemsMap.isEmpty()) {
                    throw new RuntimeException("No data fetched at all");
                }

                String allItemDataString = gson.toJson(allItemsMap);

                items = parseAPIStringToItems(allItemDataString);
                callback.accept(items);
                ItemStackBuilder.shouldReloadItems = true;
                ConfigFileUtil.writeFile("all-wynncraft-items-backup.json", allItemDataString);
            } catch (Exception e) {
                e.printStackTrace();

                loadFromBackup(callback);
            } finally {
                isCurrentlyFetchingItemData = false;
            }
        }).start();
    }

    private static void loadFromBackup(Consumer<Map<String, WynnItem>> callback) {
        try {
            String backup = ConfigFileUtil.readFile("all-wynncraft-items-backup.json");

            if (backup != null && !backup.isEmpty()) {
                items = parseAPIStringToItems(backup);
                callback.accept(items);
            }

        } catch (Exception e) {
            System.err.println("InventoryOverlay: Failed to load backup!");
            e.printStackTrace();
        }
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
