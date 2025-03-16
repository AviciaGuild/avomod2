package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.inventoryoverlay.item.DroppedBy;
import cf.avicia.avomod2.inventoryoverlay.item.WynnItem;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObtainingInfoScreen extends Screen {

    private final Screen parentScreen;
    private final WynnItem wynnItem;
    private final ItemStack itemStack;
    private final int windowWidth = 400;
    private final int windowHeight = 200;
    private final int frameSize = 20;
    private int topLeftX;
    private int topLeftY;
    private int scrollAmount = 0;
    private boolean haveRenderedButtonsBeenAdded = false;

    public ObtainingInfoScreen(Screen parentScreen, WynnItem wynnItem, ItemStack itemStack) {
        super(itemStack.getName());
        this.parentScreen = parentScreen;
        this.wynnItem = wynnItem;
        this.itemStack = itemStack;
    }

    @Override
    public void init() {
        super.init();
        this.topLeftX = this.width / 2 - windowWidth / 2;
        this.topLeftY = this.height / 2 - windowHeight / 2;
        Screens.getButtons(this).add(new ItemButtonWidget(topLeftX, topLeftY, frameSize, itemStack, wynnItem));
        Screens.getButtons(this).add(new ElevatedButtonWidget(topLeftX + windowWidth - frameSize, topLeftY, frameSize, frameSize, Text.of("§cX"), Text.of("Close"), button -> {
            this.close();
        }));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        // Draw background
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0f, 0f, 111f);
        drawContext.fill(topLeftX - 1, topLeftY - 1, topLeftX + windowWidth + 1, topLeftY + frameSize, new Color(100, 100,100).getRGB());
        drawContext.fill(topLeftX, topLeftY, topLeftX + windowWidth, topLeftY + frameSize, new Color(30, 15, 30).getRGB());
        drawContext.fill(topLeftX - 1, topLeftY  + windowHeight - frameSize, topLeftX + windowWidth + 1, topLeftY + windowHeight, new Color(100, 100,100).getRGB());
        drawContext.fill(topLeftX, topLeftY + windowHeight - frameSize, topLeftX + windowWidth, topLeftY + windowHeight, new Color(30, 15, 30).getRGB());
        drawContext.drawTextWithShadow(textRenderer, this.title, topLeftX + frameSize + 5, this.height / 2 - windowHeight / 2 + 6, 0);
        drawContext.getMatrices().pop();
        drawContext.fill(topLeftX - 1, topLeftY, topLeftX, topLeftY + windowHeight, new Color(100, 100,100).getRGB());
        drawContext.fill(topLeftX, topLeftY, topLeftX + frameSize, topLeftY + windowHeight, new Color(30, 15, 30).getRGB());
        drawContext.fill(topLeftX + windowWidth, topLeftY, topLeftX + windowWidth + 1, topLeftY + windowHeight, new Color(100, 100,100).getRGB());
        drawContext.fill(topLeftX + windowWidth - frameSize, topLeftY, topLeftX + windowWidth, topLeftY + windowHeight, new Color(30, 15, 30).getRGB());
        drawContext.fill(topLeftX + frameSize, topLeftY + frameSize, topLeftX + windowWidth - frameSize, topLeftY + windowHeight - frameSize, new Color(129, 100, 75).getRGB());
        // Draw all buttons
        // Draw all infoPair
        List<Pair<Text, ClickableWidget>> info = new ArrayList<>();
        info.add(new Pair<>(Text.of("This item has the following information about obtaining it:"), null));
        if (wynnItem.dropMeta != null) {
            info.add(new Pair<>(
                            Text.of(wynnItem.dropMeta.name + " - " + wynnItem.dropMeta.getType() + " at " + wynnItem.dropMeta.coordinates.stream().map(String::valueOf).collect(Collectors.joining(", "))),
                            null
                    )
            );
        }
        if (wynnItem.droppedBy != null) {
            Set<String> duplicateNames = new HashSet<>();
            for (DroppedBy droppedBy : wynnItem.droppedBy) {
                if (duplicateNames.contains(droppedBy.name)) {
                    continue;
                }
                duplicateNames.add(droppedBy.name);
                MutableText droppedByText = Text.literal("Dropped by: " + droppedBy.name);
                String readableCoordinates = droppedBy.coords.stream()
                        .map(integers -> integers.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")))
                        .collect(Collectors.joining("\n")
                        );
                ElevatedButtonWidget infoButton = new ElevatedButtonWidget(topLeftX, 0, frameSize, frameSize, Text.of("§b!"), Text.of("Coordinates of " + droppedBy.name + ": \n" + readableCoordinates), button -> {
                    this.haveRenderedButtonsBeenAdded = false;
                    try {
                        String joinedCoordinates = droppedBy.coords.stream()
                                .map(integers -> integers.stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(",")))
                                .collect(Collectors.joining(",")
                                );
                        ConfirmLinkScreen.open(this, new URI("https://map.wynncraft.com/?coords=" + joinedCoordinates)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1f);
                info.add(new Pair<>(droppedByText, infoButton));
            }
        }
        if (wynnItem.dropRestriction != null) {
            info.add(new Pair<>(Text.of("Drop Restriction: " + wynnItem.dropRestriction), null));
        }
        if (wynnItem.type.equals("material")) {
            info.add(new Pair<>(Text.of("Profession Material: " + wynnItem.requirements.level + " " + wynnItem.getProfessionLabel()), null));
        }
        int rowIndex = 1;
        for (Pair<Text, ClickableWidget> infoPair : info) {
            final int y = scrollAmount + this.height / 2 - windowHeight / 2 + frameSize * rowIndex++;
            if (y < topLeftY || y > topLeftY + windowHeight - frameSize) {
                continue;
            }
            drawContext.drawTextWithShadow(textRenderer, infoPair.getA(), topLeftX + frameSize + 5, y + 6, 0xffffff);
            if (!haveRenderedButtonsBeenAdded) {
                if (infoPair.getB() != null) {
                    Screens.getButtons(this).add(infoPair.getB());
                    infoPair.getB().setY(y);
                }
            }
        }
        haveRenderedButtonsBeenAdded = true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        final int scrolledBy = (int) verticalAmount * (client != null ? client.options.getGuiScale().getValue() : 1);
        scrollAmount += scrolledBy;
        for (ClickableWidget widget : Screens.getButtons(this)) {
            if (widget.getMessage().getString().equals("§b!")) {
                final int y = widget.getY() + scrolledBy;
                widget.visible = y >= topLeftY && y <= topLeftY + windowHeight - frameSize;
                widget.setY(y);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parentScreen);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
