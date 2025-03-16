package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessionHighlighter {
    private static ButtonWidget buttonWidget;
    private static final List<String> profOptions = List.of("-", "Ⓐ", "Ⓓ", "Ⓔ", "Ⓕ", "Ⓖ", "Ⓗ", "Ⓘ", "Ⓛ");
    private static int selectedProfIndex = 0;
    private static final ArrayList<Point> highlightedSlots = new ArrayList<>();

    public static void addProfessionButton(Screen screen, int scaledWidth, int scaledHeight) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("profHighlighter")) return;
        init(screen, scaledWidth, scaledHeight);
        Screens.getButtons(screen).add(buttonWidget);
        ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
            if (buttonWidget != null) {
                buttonWidget.setFocused(false);
                if (buttonWidget.isMouseOver(mouseX, mouseY) && button == 1) {
                    selectedProfIndex = (selectedProfIndex + profOptions.size() - 1) % profOptions.size();
                    buttonWidget.setMessage(Text.of(profOptions.get(selectedProfIndex)));
                    ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
                }
            }
        });
    }

    public static void render(Screen screen1, MinecraftClient client, DrawContext drawContext, int scaledWidth, int scaledHeight) {
        if (!(screen1 instanceof GenericContainerScreen) || ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("profHighlighter")) {
            return;
        }
        updateHighlightedSlots();
        final int slots = client.player != null ? client.player.currentScreenHandler.slots.size() : 0;
        final int rows = slots / 9;
        final int rowHeight = 18;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0, 0.0, 200F);
        final double rowHeightAdjustment = Math.floor(((rows - 3) * -0.5) * rowHeight);
        for (Point p : highlightedSlots) {
            int x = (scaledWidth / 2) + p.x - 88;
            int y = (int) ((scaledHeight / 2) + p.y - rowHeightAdjustment - (rows - 1) * rowHeight - 12);
            drawContext.fill(x, y, x + 17, y + 17, new Color(0, 0, 255, 100).getRGB());
        }
        drawContext.getMatrices().pop();
    }

    private static void init(Screen screen, int scaledWidth, int scaledHeight) {
        final int slots = MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.currentScreenHandler.slots.size() : 0;
        final int rows = slots / 9;
        final int rowHeight = 18;
        final double rowHeightAdjustment = Math.floor(((rows - 3) * -0.5) * rowHeight);
        int topLeftY = (int) ((scaledHeight / 2) - rowHeightAdjustment - (rows - 1) * rowHeight - 12);
        buttonWidget = new ButtonWidget((scaledWidth / 2) + 90, topLeftY, 20, 20, Text.of(profOptions.get(selectedProfIndex)), button -> {
            selectedProfIndex = (selectedProfIndex + 1) % profOptions.size();
            buttonWidget.setMessage(Text.of(profOptions.get(selectedProfIndex)));
            updateHighlightedSlots();
        }, textSupplier -> Text.literal(profOptions.get(selectedProfIndex)));
    }
    public static void updateHighlightedSlots() {
        if (MinecraftClient.getInstance().player != null) {
            DefaultedList<Slot> containerSlots = MinecraftClient.getInstance().player.currentScreenHandler.slots;
            highlightedSlots.clear();
            if (selectedProfIndex == 0) return;
            for (Slot slot : containerSlots) {
                if (slot.getStack().getName().getString().equals("Air")) continue;
                List<Text> territoryLore = slot.getStack().getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.ADVANCED);
                boolean loreContainsProf = territoryLore.stream().anyMatch(e -> e.getString().contains(profOptions.get(selectedProfIndex))) && territoryLore.stream().anyMatch(e -> e.getString().contains("Lv. Min:"));
                if (loreContainsProf) {
                    highlightedSlots.add(new Point(slot.x, slot.y));
                }
            }
        }
    }
}
