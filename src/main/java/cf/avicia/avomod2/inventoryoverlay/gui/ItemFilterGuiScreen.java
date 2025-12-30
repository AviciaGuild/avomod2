package cf.avicia.avomod2.inventoryoverlay.gui;

import cf.avicia.avomod2.inventoryoverlay.util.Comparator;
import cf.avicia.avomod2.inventoryoverlay.util.Filter;
import cf.avicia.avomod2.inventoryoverlay.util.ItemsDataHandler;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemFilterGuiScreen extends Screen {
    private final int labelMenuHeight = 30;
    private Screen previousScreen;
    private boolean itemFilterGuiPreviouslyOpened;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    private RegularButtonWidget backButton;
    private ButtonWidget addFilterButton;
    private final ArrayList<DropdownWidget> filterListOption;
    private final ArrayList<DropdownWidget> filterListValue;
    private final ArrayList<ButtonWidget> filterListComparator;
    private final ArrayList<TextFieldWidget> filterListConstant;
    private final ArrayList<RegularButtonWidget> filterListDelete;

    private final ArrayList<Filter> itemFilters = new ArrayList<>();
    public int levelMin = 0;
    public int levelMax = 110;

    private int removeIndex = -1;


    public ItemFilterGuiScreen() {
        super(Text.empty());

        filterListOption = new ArrayList<>();
        filterListValue = new ArrayList<>();
        filterListComparator = new ArrayList<>();
        filterListConstant = new ArrayList<>();
        filterListDelete = new ArrayList<>();
    }

    public void open(Screen previousScreen) {
        this.previousScreen = previousScreen;
        MinecraftClient.getInstance().setScreen(this);
        postInit();
    }

    public void postInit() {
        backButton = new RegularButtonWidget(5, 5, 30, 20, Text.literal("Back"), Text.of("Go Back"), (button) -> close());
        NumberTextFieldWidget levelMinField = new NumberTextFieldWidget(textRenderer, 115, 5, 30, 20, levelMin, integer -> levelMin = integer);
        levelMinField.setTooltip(Tooltip.of(Text.of("Minimum")));
        NumberTextFieldWidget levelMaxField = new NumberTextFieldWidget(textRenderer, 155, 5, 30, 20, levelMax, integer -> levelMax = integer);
        levelMaxField.setTooltip(Tooltip.of(Text.of("Maximum")));
        Screens.getButtons(this).add(levelMinField);
        Screens.getButtons(this).add(levelMaxField);
        addFilterButton = ButtonWidget.builder(Text.literal("Add New Filter"), (button) -> {
            int index = filterListOption.size();

            Filter filter = new Filter();
            itemFilters.add(filter);

            ButtonWidget comparator = ButtonWidget.builder(Text.literal("Matches"), b -> {
                filter.incrementComparator();
                b.setMessage(Text.literal(
                        switch (filter.comparator) {
                            case Comparator.EXISTS: yield "Matches";
                            case Comparator.NOT_EXISTS: yield "Excludes";
                            case Comparator.GTE: yield ">=";
                            case Comparator.GT: yield ">";
                            case Comparator.EQUALS: yield "=";
                            case Comparator.LTE: yield "<=";
                            case Comparator.LT: yield "<";
                        }
                ));

            }).position(380, labelMenuHeight + 5 + index * 25).size(60, 20).tooltip(Tooltip.of(Text.literal("Click to cycle"))).build();

            DropdownWidget valueWidget = new DropdownWidget(textRenderer, 125, labelMenuHeight + 7 + index * 25, 250, Text.literal(""), "", List.of(), (v) -> {
                filter.value = v;
            });
            TextFieldWidget constant = new TextFieldWidget(textRenderer, 445, labelMenuHeight + 8 + index * 25, 50, 20, Text.literal(""));
            constant.setText("0");
            constant.setTooltip(Tooltip.of(Text.of("Compared to the raw value")));
            constant.setChangedListener(c -> {
                try {
                    filter.constant = Integer.parseInt(c);
                } catch (Exception ignored) {}
            });
            DropdownWidget options = new DropdownWidget(textRenderer, 30, labelMenuHeight + 7 + index * 25, 90, Text.literal(""), "Select Sort Type", ItemsDataHandler.possibleFilters.keySet().stream().toList(), (v) -> {
                filter.setOption(v);
                valueWidget.setChoices(ItemsDataHandler.possibleFilters.get(v).stream().toList());
                valueWidget.setDefaultText("Select " + v);
                comparator.setMessage(Text.literal("Matches"));

            });
            RegularButtonWidget delete = new RegularButtonWidget(5, labelMenuHeight + 5 + index * 25, 20, 20, Text.literal("D"), Text.of("Delete"), b -> removeIndex = filterListOption.indexOf(options));

            filterListOption.add(options);
            filterListValue.add(valueWidget);
            filterListComparator.add(comparator);
            filterListConstant.add(constant);
            filterListDelete.add(delete);

            updateFilterListPositions();
        }).position(5, labelMenuHeight + 5 + filterListOption.size() * 25).size(80, 20).build();
        itemFilterGuiPreviouslyOpened = true;
    }

    public void clearFilters() {
        filterListOption.clear();
        filterListValue.clear();
        filterListComparator.clear();
        filterListConstant.clear();
        filterListDelete.clear();
        itemFilters.clear();
        levelMin = 0;
        levelMax = 110;

        updateFilterListPositions();
    }

    private void updateFilterListPositions() {
        if (itemFilterGuiPreviouslyOpened) {
            addFilterButton.setY(labelMenuHeight + 5 + filterListOption.size() * 25);

            filterListOption.forEach(i -> i.setY(labelMenuHeight + 8 + filterListOption.indexOf(i) * 25));
            filterListValue.forEach(i -> i.setY(labelMenuHeight + 8 + filterListValue.indexOf(i) * 25));
            filterListComparator.forEach(i -> i.setY(labelMenuHeight + 5 + filterListComparator.indexOf(i) * 25));
            filterListConstant.forEach(i -> i.setY(labelMenuHeight + 5 + filterListConstant.indexOf(i) * 25));
            filterListDelete.forEach(i -> i.setY(labelMenuHeight + 5 + filterListDelete.indexOf(i) * 25));
            //filterListDuplicate.forEach(i -> i.y = labelMenuHeight + 5 + filterListDuplicate.indexOf(i) * 25);
        }
    }

    public ArrayList<Filter> getItemFilters() {
        return itemFilters;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
//        this.renderBackground(context, mouseX, mouseY, delta);

        // draw filter buttons and stuff
        boolean anyOpen = false;
        for (DropdownWidget o : filterListOption) if (o.willClick(mouseX, mouseY)) anyOpen = true;
        if (anyOpen) {
            addFilterButton.render(context, 0, 0, delta); // funny band-aid fix for rendering white outline while in dropdown menu
        } else {
            addFilterButton.render(context, mouseX, mouseY, delta);
        }

        for (DropdownWidget o : filterListOption) {
            o.renderMain(context, mouseX, mouseY, delta);
        }
        for (DropdownWidget v : filterListValue) {
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().isEmpty())
                v.renderMain(context, mouseX, mouseY, delta);
        }
        for (TextFieldWidget c : filterListConstant) {
            if ((filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("identification") || filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("base")) && !(itemFilters.get(filterListConstant.indexOf(c)).comparator.ordinal() <= Comparator.NOT_EXISTS.ordinal()))
                c.render(context, mouseX, mouseY, delta);
        }
        for (ButtonWidget c : filterListComparator) {
            if (!filterListOption.get(filterListComparator.indexOf(c)).getLastChoice().isEmpty())
                c.render(context, mouseX, mouseY, delta);
        }
        filterListDelete.forEach(i -> i.render(context, mouseX, mouseY, delta));
        //filterListDuplicate.forEach(i -> i.render(context, mouseX, mouseY, delta));

        for (DropdownWidget o : filterListOption) {
            o.renderDropdown(context, mouseX, mouseY, delta);
        }
        for (DropdownWidget v : filterListValue) {
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().isEmpty())
                v.renderDropdown(context, mouseX, mouseY, delta);
        }

        // draw the label at the top
        context.getMatrices().pushMatrix();
//        context.getMatrices().translate(0, 0, 110);
//        context.fill(0, 0, width, labelMenuHeight, 0xFF555555);
        context.drawHorizontalLine(0, width, labelMenuHeight, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Item Filters").setStyle(Style.EMPTY.withBold(true)), width / 2, (labelMenuHeight - textRenderer.fontHeight) / 2, 0xFFFFAA00);
        context.drawTextWithShadow(textRenderer, Text.of("Level Range"), 50, (labelMenuHeight - textRenderer.fontHeight) / 2 + 1, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.of("-"), 148, (labelMenuHeight - textRenderer.fontHeight) / 2 + 1, 0xFFFFFFFF);
        context.getMatrices().popMatrix();

        // draw gui elements
        context.getMatrices().pushMatrix();
//        context.getMatrices().translate(0, 0, 110);
        backButton.render(context, mouseX, mouseY, delta);
        context.getMatrices().popMatrix();

//        super.render(context, mouseX, mouseY, delta);

    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        super.mouseClicked(click, doubled);

        backButton.mouseClicked(click, doubled);

        for (DropdownWidget o : filterListOption) {
            if (o.willClick(click.x(), click.y())) {
                o.mouseClicked(click, doubled);
                return false;
            }
            o.mouseClicked(click, doubled);
        }
        for (DropdownWidget v : filterListValue) {
            if (v.willClick(click.x(), click.y())) {
                if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().isEmpty())
                    v.mouseClicked(click, doubled);
                return false;
            }
            if (!filterListOption.get(filterListValue.indexOf(v)).getLastChoice().isEmpty())
                v.mouseClicked(click, doubled);
        }
        for (ButtonWidget c : filterListComparator) {
            if (!filterListOption.get(filterListComparator.indexOf(c)).getLastChoice().isEmpty())
                c.mouseClicked(click, doubled);
        }
        for (TextFieldWidget c : filterListConstant) {
            if ((filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("identification") || filterListOption.get(filterListConstant.indexOf(c)).getLastChoice().equals("base")) && !(itemFilters.get(filterListConstant.indexOf(c)).comparator.ordinal() <= Comparator.NOT_EXISTS.ordinal())) {
                c.setFocused(c.isMouseOver(click.x(), click.y()));
//                c.mouseClicked(click, doubled);
            }
        }
        filterListDelete.forEach(i -> i.mouseClicked(click, doubled));
        //filterListDuplicate.forEach(i -> i.mouseClicked(click, doubled));

        addFilterButton.mouseClicked(click, doubled);

        if (removeIndex != -1) {
            filterListOption.remove(removeIndex);
            filterListValue.remove(removeIndex);
            filterListComparator.remove(removeIndex);
            filterListConstant.remove(removeIndex);
            filterListDelete.remove(removeIndex);
            //filterListDuplicate.remove(removeIndex);
            itemFilters.remove(removeIndex);
            removeIndex = -1;

            updateFilterListPositions();
        }

        return true;
    }

    @Override
    public boolean charTyped(CharInput input) {
        super.charTyped(input);

        filterListOption.forEach(i -> i.charTyped(input));
        filterListValue.forEach(i -> i.charTyped(input));
        filterListConstant.forEach(i -> i.charTyped(input));

        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        super.keyPressed(input);

        filterListOption.forEach(i -> i.keyPressed(input));
        filterListValue.forEach(i -> i.keyPressed(input));
        filterListConstant.forEach(i -> i.keyPressed(input));

        return true;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        filterListOption.forEach(i -> i.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
        filterListValue.forEach(i -> i.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));

        return true;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(previousScreen);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
