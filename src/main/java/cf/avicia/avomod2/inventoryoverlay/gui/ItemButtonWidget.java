package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.inventoryoverlay.item.WynnItem;
import cf.avicia.avomod2.inventoryoverlay.util.InventoryOverlayUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Stream;

public class ItemButtonWidget extends ButtonWidget {
    ItemStack item;
    WynnItem wynnItem;

    public ItemButtonWidget(int x, int y, int slotSize, ItemStack item, WynnItem wynnItem) {
        super(x, y, slotSize, slotSize, Text.empty(), ButtonWidget::onPress, DEFAULT_NARRATION_SUPPLIER);
        this.item = item;
        this.wynnItem = wynnItem;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen == null) {
                return false; // This should never happen
            }
            if (button == 1) {
                InventoryOverlayUtils.openWikiURL(wynnItem, currentScreen);
            } else if (button == 0) {
                if (!(currentScreen instanceof ObtainingInfoScreen)) {
                    // Don't allow opening info screens inside of info screens
                    MinecraftClient.getInstance().setScreen(new ObtainingInfoScreen(currentScreen, wynnItem, item));
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        int minX = getX();
        int minY = getY();
        int maxX = minX + width;
        int maxY = minY + height;

        boolean hovered = (mouseX >= minX) && (mouseX <= maxX) && (mouseY >= minY) && (mouseY <= maxY);

        int outlineColor = hovered ? 0xFFC6C6C6 : 0xFFFFFFFF;
        int fillOpacity = hovered ? 0x6B000000 : 0x88000000;
        context.getMatrices().push();
        context.getMatrices().translate(0.0, 0.0, 160F);
        context.fill(minX, minY, maxX, maxY, fillOpacity | wynnItem.getBackgroundColor());
        context.drawHorizontalLine(minX, maxX, minY, outlineColor);
        context.drawHorizontalLine(minX, maxX, maxY, outlineColor);
        context.drawVerticalLine(minX, minY, maxY, outlineColor);
        context.drawVerticalLine(maxX, minY, maxY, outlineColor);

        context.drawItem(item, minX + 2, minY + 2);
        context.getMatrices().pop();
        if (isHovered() && MinecraftClient.getInstance().currentScreen != null) {
            Stream<Text> infoTooltip = InventoryOverlayUtils.isShiftDown() ? Stream.of(Text.empty(), Text.of("§7Left-click for Drop Info"), Text.of("§7Right-click to Open Wiki")) : Stream.of(Text.empty(), Text.of("§7Hold SHIFT for Hints"));
            List<Text> tooltip = Stream.concat(Stream.concat(Stream.of(Text.of(item.getName())), wynnItem.getLore().stream()), infoTooltip).toList();
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
    }
}