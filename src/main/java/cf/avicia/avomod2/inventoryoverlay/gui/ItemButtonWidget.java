package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.inventoryoverlay.item.WynnItem;
import cf.avicia.avomod2.inventoryoverlay.util.InventoryOverlayUtils;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class ItemButtonWidget extends ButtonWidget {
    ItemStack item;
    WynnItem wynnItem;

    public ItemButtonWidget(int x, int y, int slotSize, ItemStack item, WynnItem wynnItem) {
        super(x, y, slotSize, slotSize, net.minecraft.text.Text.empty(), (widget) -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.item = item;
        this.wynnItem = wynnItem;
    }


    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.active && this.visible && this.isMouseOver(click.x(), click.y())) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen == null) {
                return false; // This should never happen
            }
            if (click.button() == 1) {
                InventoryOverlayUtils.openWikiURL(wynnItem, currentScreen);
            } else if (click.button() == 0) {
                if (!(currentScreen instanceof ObtainingInfoScreen)) {
                    // Don't allow opening info screens inside of info screens
                    MinecraftClient.getInstance().setScreen(new ObtainingInfoScreen(currentScreen, wynnItem, item));
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int minX = getX();
        int minY = getY();
        int maxX = minX + width;
        int maxY = minY + height;

        boolean hovered = (mouseX >= minX) && (mouseX <= maxX) && (mouseY >= minY) && (mouseY <= maxY);

        int outlineColor = hovered ? 0xFFC6C6C6 : 0xFFFFFFFF;
        int fillOpacity = hovered ? 0x6B000000 : 0x88000000;
        context.getMatrices().pushMatrix();
//        context.getMatrices().translate(0.0, 0.0, 160F);
        context.fill(minX, minY, maxX, maxY, fillOpacity | wynnItem.getBackgroundColor());
        context.drawHorizontalLine(minX, maxX, minY, outlineColor);
        context.drawHorizontalLine(minX, maxX, maxY, outlineColor);
        context.drawVerticalLine(minX, minY, maxY, outlineColor);
        context.drawVerticalLine(maxX, minY, maxY, outlineColor);

        context.drawItem(item, minX + 2, minY + 2);
        context.getMatrices().popMatrix();
        if (isHovered() && MinecraftClient.getInstance().currentScreen != null) {
            Stream<net.minecraft.text.Text> infoTooltip = Utils.isShiftDown() ? Stream.of(net.minecraft.text.Text.empty(), net.minecraft.text.Text.of("§7Left-click for Drop Info"), net.minecraft.text.Text.of("§7Right-click to Open Wiki")) : Stream.of(net.minecraft.text.Text.empty(), net.minecraft.text.Text.of("§7Hold SHIFT for Hints"));
            List<net.minecraft.text.Text> tooltip = Stream.concat(Stream.concat(Stream.of(net.minecraft.text.Text.of(item.getName())), wynnItem.getLore().stream()), infoTooltip).toList();
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void onPress(AbstractInput input) {
    }
}