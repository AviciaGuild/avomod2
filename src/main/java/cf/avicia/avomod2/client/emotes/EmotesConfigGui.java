package cf.avicia.avomod2.client.emotes;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.core.CustomFile;
import cf.avicia.avomod2.inventoryoverlay.gui.DropdownWidget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cf.avicia.avomod2.client.emotes.EmotesGui.*;

public class EmotesConfigGui extends Screen {
    private boolean initialized = false;
    private final KeyBinding keyBinding;
    private final List<String> emotes = new ArrayList<>();

    private final List<DropdownWidget> dropdownWidgetList = new ArrayList<>();
    protected EmotesConfigGui(KeyBinding keyBinding) {
        super(Text.of("Emotes Config"));
        this.keyBinding = keyBinding;
    }

    private void initialize() {
        if (initialized) return;
        initialized = true;

        CustomFile customFile = new CustomFile(ConfigsHandler.getConfigPath("emotes"));
        JsonObject savedEmotes = customFile.readJson();
        List<String> availableEmotes = new ArrayList<>();
        availableEmotes.add("");

        emotes.clear();
        if (savedEmotes.has("favorites")) {
            JsonArray favorites = savedEmotes.get("favorites").getAsJsonArray();

            for (JsonElement favorite : favorites) {
                emotes.add(favorite.getAsString());
            }
            emotes.addAll(Collections.nCopies(squareCount - favorites.size(), ""));
        } else {
            emotes.addAll(Collections.nCopies(squareCount, ""));
        }
        if (savedEmotes.has("unlocked")) {
            JsonArray unlocked = savedEmotes.get("unlocked").getAsJsonArray();

            for (JsonElement emote : unlocked) {
                availableEmotes.add(emote.getAsString());
            }
        }

        for (int i = 0; i < squareCount; i++) {
            int finalI = i;
            dropdownWidgetList.add(new DropdownWidget(MinecraftClient.getInstance().textRenderer, 0, 0, squareSize, Text.literal(emotes.get(finalI)), "Emote " + (finalI + 1), availableEmotes, (v) -> {
                emotes.set(finalI, v);
            }, true));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        initialize();
        final int middleX = width / 2;
        final int middleY = height / 2;
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(2f, 2f, context.getMatrices());
        context.drawCenteredTextWithShadow(textRenderer, Text.of("Configure emotes"), middleX / 2, (middleY - radius - squareSize) / 2, Color.BLUE.getRGB());
        context.getMatrices().popMatrix();

        final List<Pair<Integer, Integer>> squarePositions = new ArrayList<>();
        for (int i = 0; i < squareCount; i++) {
            double angle = Math.toRadians(i * ((double) 360 / squareCount)); // 8 directions, every 45 degrees
            int x = (int) (middleX + radius * Math.cos(angle));
            int y = (int) (middleY + radius * Math.sin(angle));
            squarePositions.add(new Pair<>(x, y));
        }

        for (int i = 0; i < squarePositions.size(); i++) {
            Pair<Integer, Integer> pos = squarePositions.get(i);
            int squareX = pos.getLeft() - squareSize / 2;
            int squareY = pos.getRight() - squareSize / 2;

            context.fill(squareX, squareY, squareX + squareSize, squareY + squareSize, Color.BLACK.getRGB());

        dropdownWidgetList.get(i).setX(pos.getLeft() - squareSize / 2);
        dropdownWidgetList.get(i).setY(pos.getRight() - 5);
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.of(Integer.toString(i + 1)),
                    pos.getLeft() - squareSize / 2 + 6,
                    pos.getRight() - squareSize / 2 + 4,
                    Color.WHITE.getRGB()
            );
        }

        for (DropdownWidget dropdownWidget : dropdownWidgetList) {
            dropdownWidget.renderMain(context, mouseX, mouseY, delta);
            dropdownWidget.renderDropdown(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        super.mouseClicked(click, doubled);

        for (DropdownWidget dropdownWidget : dropdownWidgetList) {
            if (dropdownWidget.willClick(click.x(), click.y())) {
                dropdownWidget.mouseClicked(click, doubled);
                return false;
            }
            dropdownWidget.mouseClicked(click, doubled);
        }

        return true;
    }

    @Override
    public boolean charTyped(CharInput input) {
        super.charTyped(input);
        dropdownWidgetList.forEach(dropdownWidget -> dropdownWidget.charTyped(input));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        super.keyPressed(input);
        dropdownWidgetList.forEach(dropdownWidget -> dropdownWidget.keyPressed(input));
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        dropdownWidgetList.forEach(dropdownWidget -> dropdownWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        saveSettings();
        EmotesGui.updateFromConfig();
        MinecraftClient.getInstance().setScreen(new EmotesGui(keyBinding));
    }

    private void saveSettings() {
        CustomFile customFile = new CustomFile(ConfigsHandler.getConfigPath("emotes"));
        JsonObject savedEmotes = customFile.readJson();

        if (!savedEmotes.has("favorites")) {
            savedEmotes.add("favorites", new JsonArray());
        }

        JsonArray newFavorites = new JsonArray();

        for (String emote : emotes) {
            newFavorites.add(emote);
        }

        savedEmotes.add("favorites", newFavorites);
        customFile.writeJson(savedEmotes);
    }
}
