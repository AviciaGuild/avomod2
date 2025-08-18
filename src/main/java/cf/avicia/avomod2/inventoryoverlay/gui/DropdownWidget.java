package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.inventoryoverlay.util.InventoryOverlayUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class DropdownWidget extends TextFieldWidget {
    private final TextRenderer textRenderer;

    private List<String> choices;
    private List<String> visualChoices;
    private ArrayList<String> validChoices;
    private ArrayList<String> visualValidChoices;
    private String lastChoice;
    private String visualLastChoice;
    private String defaultText;
    private int maxShown;
    private int scrollAmount;

    private final Consumer<String> onUpdate;
    private boolean updateOnType = false;

    public DropdownWidget(TextRenderer textRenderer, int x, int y, int width, Text text, String defaultText, List<String> choices, Consumer<String> onUpdate) {
        super(textRenderer, x, y - 6, width, 14, text);
        this.textRenderer = textRenderer;
        this.onUpdate = onUpdate;

        this.choices = choices;

        this.visualChoices = choices;
        this.defaultText = defaultText;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        validChoices = new ArrayList<>(choices);
        visualValidChoices = new ArrayList<>(visualChoices);
        updateMaxShown();
    }
    public DropdownWidget(TextRenderer textRenderer, int x, int y, int width, Text text, String defaultText, List<String> choices, Consumer<String> onUpdate, boolean updateOnType) {
        this(textRenderer, x, y, width, text, defaultText, choices, onUpdate);
        this.updateOnType = updateOnType;
        lastChoice = text.getString();
        visualLastChoice = text.getString();
        setText(visualLastChoice);
    }

    public void setChoices(List<String> newChoices) {
        choices = newChoices;
        visualChoices = newChoices;
        lastChoice = "";
        visualLastChoice = "";
        setText(visualLastChoice);
        scrollAmount = 0;
    }

    @Override
    public void setText(String text) {
        super.setText(updateOnType ? text : InventoryOverlayUtils.toUpperCamelCaseWithSpaces(text));
    }

    public void setDefaultText(String newDefaultText) {
        defaultText = newDefaultText;
    }

    public String getLastChoice() {
        return lastChoice;
    }

    private void updateShownChoices() {
        validChoices.clear();
        visualValidChoices.clear();
        if (isFocused()) {
            if (getText().isEmpty()) {
                validChoices = new ArrayList<>(choices);
                visualValidChoices = new ArrayList<>(visualChoices);
            } else {
                for (String choice : visualChoices) {
                    if (choice.toLowerCase().contains(getText().toLowerCase().replace(" ", ""))) {
                        validChoices.add(choices.get(visualChoices.indexOf(choice)));
                        visualValidChoices.add(choice);
                    }
                }
                validChoices.sort(Comparator.comparing(s -> s.length() - getText().length()));
                visualValidChoices.sort(Comparator.comparing(s -> s.length() - getText().length()));
            }
        }
        updateScrollLimits();
    }

    private void updateMaxShown() {
        if (MinecraftClient.getInstance().currentScreen == null) return;
        maxShown = (int) ((double) (MinecraftClient.getInstance().currentScreen.height - (this.getY() + this.height)) / (double) (this.height + 1));
    }

    private void updateScrollLimits() {
        if (scrollAmount > validChoices.size() - maxShown) scrollAmount = validChoices.size() - maxShown;
        if (scrollAmount < 0) scrollAmount = 0;
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    // must be called manually
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isFocused()) {
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i++) {
                if (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() + this.height + ((this.height + 1) * i) && mouseY < this.getY() + this.height + ((this.height + 1) * (i + 1))) {
                    lastChoice = validChoices.get(i + scrollAmount);
                    visualLastChoice = visualValidChoices.get(i + scrollAmount);
                    onUpdate.accept(lastChoice);

                    this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                }
            }
        }
        setFocused(isMouseOver(mouseX, mouseY));
//        if (!updateOnType) {
            if (isFocused()) {
                setText("");
                validChoices = new ArrayList<>(choices);
                visualValidChoices = new ArrayList<>(visualChoices);
            } else {
                setText(visualLastChoice);
            }
//        }

        return true;
    }

    public boolean willClick(double mouseX, double mouseY) {
        return isFocused() && (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getX() + this.height) && mouseY < this.getY() + this.height + ((this.height + 1) * (validChoices.size() + 1));
    }

    // must be called manually
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        if (isFocused() && !validChoices.isEmpty() && (keyCode == 257 || keyCode == 335)) { // Enter key
            lastChoice = validChoices.getFirst();
            visualLastChoice = visualValidChoices.getFirst();
            onUpdate.accept(lastChoice);
            setText(visualLastChoice);
            setFocused(false);
        }
        updateShownChoices();
        return true;
    }

    // must be called manually
    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (updateOnType) {
            visualLastChoice = getText();
            lastChoice = getText();
            onUpdate.accept(getText());
        }
        updateShownChoices();
        return true;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        if (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY <= this.getY() + ((this.height + 1) * (Math.min(validChoices.size(), maxShown) + 1))) {
            // mouse is in scroll area
            if (validChoices.size() > maxShown) {
                // should be able to scroll
                scrollAmount -= (int) verticalAmount;
                updateScrollLimits();
            }
        }

        return true;
    }


    // must be called manually
    // called at normal render
    public void renderMain(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.fill(getX() - 1, getY() - 3, getX() + getWidth() + 1, getY() + getHeight() + 3, isFocused() ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
        context.fill(getX(), getY() - 2, getX() + getWidth(), getY() + getHeight() + 2, Color.BLACK.getRGB());

        if (lastChoice.isEmpty() && !isFocused() && !(updateOnType && !getText().isEmpty()))
            context.drawTextWithShadow(textRenderer, InventoryOverlayUtils.toUpperCamelCaseWithSpaces(defaultText), this.getX() + 3, this.getY() + 3, 0x666666);
    }

    // must be called manually
    // called after other render calls
    public void renderDropdown(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 200);
        if (this.isFocused() && !validChoices.isEmpty()) {
            context.fill(this.getX() - 1, this.getY() + this.height, this.getX() + this.width + 1, this.getY() + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)) + 1, 0xFFA0A0A0);
            context.fill(this.getX(), this.getY() + this.height + 1, this.getX() + this.width, this.getY() + this.height + ((this.height + 1) * Math.min(validChoices.size(), maxShown)), 0xFF000000);

            // draw highlight under mouse
            for (int i = 0; i < Math.min(validChoices.size(), maxShown); i++) {
                if (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() + this.height + ((this.height + 1) * i) && mouseY < this.getY() + this.height + ((this.height + 1) * (i + 1))) {
                    context.fill(this.getX(), this.getY() + this.height + ((this.height + 1) * i) + 1, this.getX() + this.width, this.getY() + this.height + ((this.height + 1) * (i + 1)), 0xFF212121);
                }
            }

            // draw dividing lines
            for (int i = 0; i < Math.min(validChoices.size(), maxShown) - 1; i++) {
                context.drawHorizontalLine(this.getX() + 3, this.getX() + this.width - 4, this.getY() + this.height + ((this.height + 1) * (i + 1)), 0xFFA0A0A0);
            }

            // draw choice text
            for (int i = 0; i < Math.min(visualValidChoices.size(), maxShown); i++) {
                String finalText;
                if (textRenderer.getWidth(visualValidChoices.get(i + scrollAmount)) > this.width - 8) {
                    finalText = textRenderer.trimToWidth(visualValidChoices.get(i + scrollAmount), this.width - 14) + "...";
                } else {
                    finalText = visualValidChoices.get(i + scrollAmount);
                }
                //String finalText = textRenderer.trimToWidth(validChoices.get(i), this.width - 8);
                context.drawTextWithShadow(textRenderer, updateOnType ? finalText : InventoryOverlayUtils.toUpperCamelCaseWithSpaces(finalText), this.getX() + 4, this.getY() + this.height + ((this.height + 1) * i) + 4, 0xFFFFFFFF);
            }

            // draw scroll bar if needed
            if (validChoices.size() > maxShown) {
                context.drawVerticalLine(this.getX() + this.width - 1, this.getY() + this.height, this.getY() + ((this.height + 1) * (maxShown + 1)) - 1, 0xFF303030);
                int scrollBarPixels = ((this.height + 1) * (maxShown + 1)) - 1 - this.height;
                context.drawVerticalLine(this.getX() + this.width - 1, this.getY() + this.height + (int) (scrollBarPixels * ((double) scrollAmount / validChoices.size())), this.getY() + this.height + (int) (scrollBarPixels * ((double) (scrollAmount + maxShown) / validChoices.size())), 0xFFFFFFFF);
            }
        }
        context.getMatrices().pop();
    }
}
