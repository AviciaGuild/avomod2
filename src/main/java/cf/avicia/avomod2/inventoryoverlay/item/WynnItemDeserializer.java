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
            WynnItem wynnItem = context.deserialize(value, WynnItem.class);
            items.put(key, wynnItem);
            wynnItem.name = key;
            if (wynnItem.identifications != null) {
                for (String identification : wynnItem.identifications.keySet()) {
                    ItemsDataHandler.possibleFilters.get("identification").add(identification);
                }
            }
            if (wynnItem.base != null) {
                for (String identification : wynnItem.base.keySet()) {
                    ItemsDataHandler.possibleFilters.get("base").add(identification);
                }
            }
            if (wynnItem.majorIds != null) {
                for (String identification : wynnItem.majorIds.keySet()) {
                    ItemsDataHandler.possibleFilters.get("majorId").add(identification);
                }
            }
            if (wynnItem.tier != null) {
                ItemsDataHandler.possibleFilters.get("tier").add(wynnItem.tier);
            }
            if (wynnItem.rarity != null) {
                ItemsDataHandler.possibleFilters.get("rarity").add(wynnItem.rarity);
            }
            if (wynnItem.type != null) {
                ItemsDataHandler.possibleFilters.get("type").add(wynnItem.type);
            }
            if (wynnItem.armourType != null) {
                ItemsDataHandler.possibleFilters.get("type").add(wynnItem.armourType);
            }
            if (wynnItem.weaponType != null) {
                ItemsDataHandler.possibleFilters.get("type").add(wynnItem.weaponType);
            }
            if (wynnItem.accessoryType != null) {
                ItemsDataHandler.possibleFilters.get("type").add(wynnItem.accessoryType);
            }
            if (wynnItem.restrictions != null) {
                ItemsDataHandler.possibleFilters.get("restriction").add(wynnItem.restrictions);
            }
            if (!wynnItem.getProfessions().isEmpty()) {
                ItemsDataHandler.possibleFilters.get("profession").addAll(wynnItem.getProfessions());
            }
        }

        return items;
    }
}

