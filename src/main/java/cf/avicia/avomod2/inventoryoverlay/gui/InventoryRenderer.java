package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.inventoryoverlay.util.InventoryOverlayUtils;
import cf.avicia.avomod2.inventoryoverlay.util.ItemStackBuilder;
import cf.avicia.avomod2.inventoryoverlay.util.ItemsDataHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryRenderer {
    private static InventoryOverlay inventoryOverlay;

    public static void init(Screen screen, int scaledWidth, int scaledHeight) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("itemOverlay")) return;
        inventoryOverlay = new InventoryOverlay(ItemStackBuilder.getAllItems(ItemsDataHandler.items), screen, scaledWidth, scaledHeight, s -> {
            InventoryOverlay.currentSearchText = s;
            updateHighlightedSlots();
        });
        inventoryOverlay.redraw();
    }

    private static final Set<Integer> highlightedSlots = new HashSet<>();

    public static void render(Screen screen1, MinecraftClient client, DrawContext drawContext, int scaledWidth, int scaledHeight) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("itemOverlay")) return;
        if (client.player == null) {
            return;
        }
        updateHighlightedSlots();
        List<Slot> slots = client.player.currentScreenHandler.slots;
        final int slotSize = 18;

        final int searchMissColor = new Color(0, 0, 0, 150).getRGB();
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 358F);
        int screenX = ((HandledScreen<?>) screen1).x;
        int screenY = ((HandledScreen<?>) screen1).y;
        highlightMatchingSLots(drawContext, slots, screenX, screenY, slotSize, searchMissColor);
        drawContext.getMatrices().pop();
        if (inventoryOverlay.shouldRenderItems()) {
            inventoryOverlay.renderBackground(drawContext);
            inventoryOverlay.redraw();
        }
    }

    public static ActionResult onMouseScroll(double mouseX, double mouseY, double verticalAmount) {
        if (inventoryOverlay != null && inventoryOverlay.shouldRenderItems()) {
            return inventoryOverlay.onMouseScroll(mouseX, mouseY, verticalAmount);
        }
        return ActionResult.SUCCESS;
    }

    private static void highlightMatchingSLots(DrawContext drawContext, List<Slot> slots, int screenX, int screenY, int slotSize, int searchMissColor) {
        if (!InventoryOverlay.highlightSearchedString) {
            return;
        }
        for (Slot slot : slots) {
            int x = slot.x + screenX - 1;
            int y = slot.y + screenY - 1;
            if (!highlightedSlots.contains(slot.id)) {
                drawContext.fill(x, y, x + slotSize, y + slotSize, searchMissColor);
            }
        }
    }

    public static void updateHighlightedSlots() {
        if (MinecraftClient.getInstance().player != null) {
            DefaultedList<Slot> containerSlots = MinecraftClient.getInstance().player.currentScreenHandler.slots;
            highlightedSlots.clear();
            if (InventoryOverlay.currentSearchText.isEmpty()) return;
            for (Slot slot : containerSlots) {
                if (InventoryOverlayUtils.itemMatches(slot.getStack(), InventoryOverlay.currentSearchText, InventoryOverlay.searchLore)) {
                    highlightedSlots.add(slot.id);
                }
            }
        }

    }
}
