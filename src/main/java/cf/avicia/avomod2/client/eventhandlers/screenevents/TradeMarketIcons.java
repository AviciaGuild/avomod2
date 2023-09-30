package cf.avicia.avomod2.client.eventhandlers.screenevents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Optional;


public class TradeMarketIcons {

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        if (screen1 instanceof GenericContainerScreen && screen1.getTitle().getString().equals("Trade Overview")) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                DefaultedList<Slot> containerSlots = player.currentScreenHandler.slots;
                for (Slot slot : containerSlots) {
                    // Ignore items in player's inventory
                    if (slot.inventory.equals(MinecraftClient.getInstance().player.getInventory())) continue;
                    ItemStack itemStack = slot.getStack();
                    String itemName = itemStack.getName().getString();
                    if (itemName.equals("Air")) continue;
                    List<Text> lore = itemStack.getTooltip(player, TooltipContext.Default.ADVANCED);
                    Optional<Text> soldOut = lore.stream().filter(line -> line.getString().contains("Sold Out")).findFirst();
                    if (itemStack.toString().contains("golden_shovel")) {
                        if (itemName.contains("Selling")) {
                            if (soldOut.isPresent()) {
                                itemStack.setDamage(20); // Green Checkmark
                            } else {
                                itemStack.setDamage(19); // Yellow Checkmark
                            }
                        }
                        if (itemName.contains("Buying")) {
                            itemStack.setDamage(22); // Green Plus
                        }
                    }
                }
            }
        }
    }
}
