package cf.avicia.avomod2.customitemtextures;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomItemTextures {
    final static int foodScrollLimit = 55; // Food is around 70 and scrolls are around 40 (this is very unscientific)

    private static boolean isCrafted(List<Text> loreLines) {
        return loreLines.stream().anyMatch(lineText -> lineText.getString().contains("Crafted"));
    }

    private static boolean isFood(ItemStack itemStack, List<Text> loreLines) {
        return isCrafted(loreLines) && itemStack.isOf(Registries.ITEM.get(Identifier.of("minecraft:diamond_axe"))) && itemStack.getDamage() > foodScrollLimit;
    }

    private static boolean isScroll(ItemStack itemStack, List<Text> loreLines) {
        return isCrafted(loreLines) && itemStack.isOf(Registries.ITEM.get(Identifier.of("minecraft:diamond_axe"))) && itemStack.getDamage() <= foodScrollLimit;
    }
    private static boolean isPotion(ItemStack itemStack, List<Text> loreLines) {
        return isCrafted(loreLines) && itemStack.isOf(Registries.ITEM.get(Identifier.of("minecraft:potion")));
    }

    private static boolean isOfType(ItemStack itemStack, List<Text> loreLines, String type) {
        return switch (type.toLowerCase()) {
            case "food" -> isFood(itemStack, loreLines);
            case "scroll" -> isScroll(itemStack, loreLines);
            case "potion" -> isPotion(itemStack, loreLines);
            default -> false;
        };
    }

    private static boolean hasCustomTextureApplied(ItemStack itemStack) {
        Identifier itemModel = itemStack.get(DataComponentTypes.ITEM_MODEL);
        return itemModel != null && itemModel.getNamespace().equals("avomod2");
    }

    private static boolean isEligible(ItemStack itemStack) {
        // This method is used to reduce lag, couldn't think of a neat solution
        Identifier itemModel = itemStack.get(DataComponentTypes.ITEM_MODEL);
        return !hasCustomTextureApplied(itemStack) && (itemModel != null && (itemModel.getPath().equals("diamond_axe") || itemModel.getPath().equals("potion")));
    }

    public static void applyCustomTexture(ItemStack itemStack) {
        if (!ConfigsHandler.getConfigBoolean("customTexturesCraftedConsumables")) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            String itemName = itemStack.getName().getString();
            if (itemName.equals("Air") || !isEligible(itemStack)) return;
            List<Text> loreLines = itemStack.getTooltip(Item.TooltipContext.DEFAULT, player, TooltipType.ADVANCED);
            if (!isCrafted(loreLines)) return;
            for (CustomItem customItem : CustomItemData.customItems) {
                if (isOfType(itemStack, loreLines, customItem.type())) {
                    if (doesLoreContainRegexes(customItem.lores(), loreLines) && doesNameMatchRegexes(customItem.names(), itemName)) {
                        itemStack.set(DataComponentTypes.ITEM_MODEL, Identifier.of("avomod2:" + customItem.texture()));
                        break;
                    }
                }
            }
        }
    }

    private static boolean doesLoreContainRegexes(List<String> regexes, List<Text> loreLines) {
        // Check if the current line matches the regex
        return regexes.stream().allMatch(regex ->
                loreLines.stream().anyMatch(lineText -> {
                    String line = lineText.getString();
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    return matcher.find();  // Check if the current line matches the regex
                })
        );
    }

    private static boolean doesNameMatchRegexes(List<String> regexes, String name) {
        for (String regex : regexes) {
            if (!Pattern.matches(regex, name)) {
                return false; // If any regex doesn't match, return false
            }
        }
        return true; // All regexes matched
    }
}
