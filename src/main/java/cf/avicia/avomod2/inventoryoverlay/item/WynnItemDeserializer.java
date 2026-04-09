package cf.avicia.avomod2.inventoryoverlay.item;

import cf.avicia.avomod2.inventoryoverlay.util.ItemsDataHandler;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class WynnItemDeserializer implements JsonDeserializer<Map<String, WynnItem>> {
    @Override
    public Map<String, WynnItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Map<String, WynnItem> items = new HashMap<>();
        try {
            ItemsDataHandler.possibleFilters.put("type", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("rarity", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("tier", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("profession", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("identification", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("base", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("restriction", new HashSet<>());
            ItemsDataHandler.possibleFilters.put("majorId", new HashSet<>());
            ItemsDataHandler.possibleFilters.get("majorId").add("any");
            ItemsDataHandler.possibleFilters.get("identification").addAll(List.of("durabilityModifier", "strengthRequirement", "dexterityRequirement", "intelligenceRequirement", "defenceRequirement", "agilityRequirement", "duration", "charges", "ingredientEffectiveness"));

            JsonObject obj = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                try {
                    WynnItem wynnItem = context.deserialize(value, WynnItem.class);
                    if (wynnItem == null) {
                        continue;
                    }
                    items.put(key, wynnItem);
                    wynnItem.name = key;
                    
                    if (wynnItem.identifications != null) {
                        for (String identification : wynnItem.identifications.keySet()) {
                            try {
                                ItemsDataHandler.possibleFilters.get("identification").add(identification);
                            } catch (Exception e) {
                                // Skip
                            }
                        }
                    }
                    if (wynnItem.base != null) {
                        for (String identification : wynnItem.base.keySet()) {
                            try {
                                ItemsDataHandler.possibleFilters.get("base").add(identification);
                            } catch (Exception e) {
                                // Skip
                            }
                        }
                    }
                    if (wynnItem.majorIds != null) {
                        for (String identification : wynnItem.majorIds.keySet()) {
                            try {
                                ItemsDataHandler.possibleFilters.get("majorId").add(identification);
                            } catch (Exception e) {
                                // Skip
                            }
                        }
                    }
                    if (wynnItem.tier != null) {
                        try {
                            ItemsDataHandler.possibleFilters.get("tier").add(wynnItem.tier);
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    if (wynnItem.tier != null) {
                        try {
                            ItemsDataHandler.possibleFilters.get("rarity").add(wynnItem.tier);
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    if (wynnItem.type != null) {
                        try {
                            ItemsDataHandler.possibleFilters.get("type").add(wynnItem.type);
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    if (wynnItem.subType != null) {
                        try {
                            ItemsDataHandler.possibleFilters.get("type").add(wynnItem.subType);
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    if (wynnItem.restrictions != null) {
                        try {
                            ItemsDataHandler.possibleFilters.get("restriction").add(wynnItem.restrictions);
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                    try {
                        List<String> professions = wynnItem.getProfessions();
                        if (!professions.isEmpty()) {
                            ItemsDataHandler.possibleFilters.get("profession").addAll(professions);
                        }
                    } catch (Exception e) {
                        // Skip profession detection if it fails
                    }
                } catch (Exception e) {
                    // Continue with next item
                }
            }
        } catch (Exception e) {
            // Continue with next item
        }

        return items;
    }
}

