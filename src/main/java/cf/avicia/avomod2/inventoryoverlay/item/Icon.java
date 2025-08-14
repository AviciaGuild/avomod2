package cf.avicia.avomod2.inventoryoverlay.item;

import java.util.List;
import java.util.Map;

public class Icon {
    public Object value; // Can be a Map<String, String> or a String
    public String format;


    public IconValue getMap() {
        IconValue item = new IconValue();
        if (value instanceof Map<?, ?> rawMap) {
            item.id = (String) rawMap.get("id");
            item.name = (String) rawMap.get("name");

            Object cmdObj = rawMap.get("customModelData");
            CustomModelData cmd = new CustomModelData();

            if (cmdObj instanceof Map<?, ?> cmdMap) {
                Object range = cmdMap.get("rangeDispatch");
                if (range instanceof List<?> list) {
                    cmd.rangeDispatch = list.stream()
                            .map(v -> ((Number) v).floatValue())
                            .toList();
                }
            } else if (cmdObj instanceof Number singleValue) {
                // Special case: just a single float, wrap it in a list
                cmd.rangeDispatch = List.of(singleValue.floatValue());
            }

            item.customModelData = cmd;
            return item;
        }
        return null;
    }

    public String getString() {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
