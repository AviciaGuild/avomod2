package cf.avicia.avomod2.client.configs;

import cf.avicia.avomod2.AvoMod2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigsGui extends Screen {
    public final int settingLineHeight = 27;
    public final int startingHeight = 85;
    public final int settingHeight = 23;
    public List<ConfigsCategory> categories = new ArrayList<>();
    // buttonList exists too, doesn't need to be created
    public ArrayList<Element> buttonList = new ArrayList<>();
    public ArrayList<ConfigsTextField> textFieldsList = new ArrayList<>();
    public String selectedCategory, savedCategory;
    public Map<String, ArrayList<ConfigsSection>> totalSections = new HashMap<>();
    public int scrollSections; // the index of the first section to be displayed

    public TextFieldWidget searchTextField;
    public boolean textFieldIsFocused = false;

    private MatrixStack matrices;

    public ConfigsGui() {
        super(Text.of("AvoMod Configs"));
    }

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.matrices = matrices;
        // Makes blur
        this.renderBackground(matrices);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        drawCenteredText(matrices, textRenderer, "Avomod Configs", this.width / 4 + 1, 11, 0x444444);
        drawCenteredText(matrices, textRenderer, "Avomod Configs", this.width / 4, 10, 0x1B33CF);
        matrices.pop();

        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();

        if (searchTextField.getText().length() > 0) {
            drawSections(matrices, getSectionsBySearch());
        } else if (!selectedCategory.equals("All")) {
            drawSections(matrices, totalSections.get(selectedCategory));
        }

        // Draw all text field inputs
//        for (ConfigsTextField textField : this.textFieldsList) {
//            textField.drawTextBox();
//        }

//        searchTextField.drawTextBox();
        if (searchTextField.getText().length() == 0 && !searchTextField.isFocused())
            drawTextWithShadow(matrices,textRenderer, Text.of("Type here to search..."), this.width / 16 + 4, startingHeight - settingHeight - 6, Color.DARK_GRAY.getRGB());

        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawSections(MatrixStack matrices, ArrayList<ConfigsSection> sectionsList) {
        drawVerticalLine(matrices, this.width / 16 + 110,  startingHeight - 10, this.height - 10, Color.WHITE.getRGB());
        ArrayList<ConfigsSection> sectionsToShow = new ArrayList<>(sectionsList.subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), sectionsList.size())));

        for (ConfigsSection configsSection : sectionsToShow) {
            int index = sectionsToShow.indexOf(configsSection);
            boolean showLine = sectionsList.indexOf(configsSection) != sectionsList.size() - 1;
            configsSection.drawSection(this, this.width / 16 + 118, startingHeight + (settingHeight + settingLineHeight + 3) * index, showLine);
        }

        if (sectionsToShow.size() == 0) {
            drawTextWithShadow(matrices, textRenderer, Text.of("[No Settings Found]"), this.width / 16 + 118, startingHeight, new Color(127, 127, 127).getRGB());
        }

        if (sectionsToShow.size() < sectionsList.size()) { // if not all configs fit on screen
            double segmentHeight = (double) ((height / 16 * 15) - startingHeight) / sectionsList.size();
            drawVerticalLine(matrices,this.width / 16 * 15 + 5,  startingHeight, height / 16 * 15, Color.DARK_GRAY.getRGB());
            drawVerticalLine(matrices, this.width / 16 * 15 + 5,  startingHeight + (int) (segmentHeight * scrollSections), startingHeight + (int) (segmentHeight * (scrollSections + sectionsToShow.size())), new Color(32, 110, 225).getRGB());
        }

        buttonList.addAll(categories);
    }

    // sets the selected category
    public void setCategory(String title) {
        scrollSections = 0;
        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();
        buttonList.addAll(categories);

        if (title.equals("All")) {
            savedCategory = selectedCategory;
        }
        selectedCategory = title;

        for (ConfigsCategory category : categories) {
            category.enabled = category.title.equals(title);
        }
    }

//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//    }

    @Override
    public void init() {
        super.init();
        scrollSections = 0;
        this.totalSections = new HashMap<>();
        this.categories = new ArrayList<>();
        selectedCategory = "";

        for (Config config : AvoMod2.configsArray) {
            this.addSection(config);
        }

        setCategory(categories.get(0).title);
        searchTextField = new SearchTextField(matrices, textRenderer, width / 16, startingHeight - settingHeight - 10, 200, 17, this);
//        searchTextField.setFocused(true);
//        searchTextField.setCanLoseFocus(false);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String oldCategory = selectedCategory;

        if (oldCategory.equals("All")) {
            oldCategory = savedCategory;
        }

        super.resize(client, width, height);
        this.init();
        setCategory(oldCategory);
    }

//    @Override
//    public void handleMouseInput() throws IOException {
//        super.handleMouseInput();
//
//        int scrollAmount = Mouse.getDWheel() / 120;
//
//        if (scrollAmount != 0) {
//            try {
//                this.scroll(scrollAmount, Mouse.getX());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public ArrayList<ConfigsSection> getSectionsBySearch() {
        return getSectionsBySearch(selectedCategory);
    }

    public ArrayList<ConfigsSection> getSectionsBySearch(String category) {
        String search = searchTextField.getText();
        ArrayList<ConfigsSection> returnSections = new ArrayList<>();

        ArrayList<ConfigsSection> allSections = new ArrayList<>();
        if (category.equals("All")) {
            for (String key : totalSections.keySet()) allSections.addAll(totalSections.get(key));
        } else {
            allSections = totalSections.get(category);
        }

        for (ConfigsSection section : allSections) {
            if (section.title.toLowerCase().contains(search.toLowerCase())) returnSections.add(section);
        }

        return returnSections;
    }

//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//
//        for (ConfigsTextField textField : this.textFieldsList) {
//            textField.mouseClicked(mouseX, mouseY, mouseButton);
//        }
//        searchTextField.mouseClicked(mouseX, mouseY, mouseButton);
//    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.keyPressed(keyCode, scanCode, modifiers);
        }
        searchTextField.keyPressed(keyCode, scanCode, modifiers);

        if (searchTextField.isFocused()) {
            scrollSections = 0;
        }

        if (keyCode == 15) {
            if (hasShiftDown()) {
                previousCategory();
            } else {
                nextCategory();
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void addButton(ConfigsButton button) {
        buttonList.add(button);
    }

    public void addTextField(ConfigsTextField textField) {
        textFieldsList.add(textField);
    }

    private void nextCategory() {
        int currentIndex = categories.stream().map(e -> e.title).toList().indexOf(selectedCategory);
        if (currentIndex == categories.size() - 1) {
            setCategory(categories.get(0).title);
        } else {
            setCategory(categories.get(currentIndex + 1).title);
        }
    }

    private void previousCategory() {
        int currentIndex = categories.stream().map(e -> e.title).toList().indexOf(selectedCategory);
        if (currentIndex == 0) {
            setCategory(categories.get(categories.size() - 1).title);
        } else {
            setCategory(categories.get(currentIndex - 1).title);
        }
    }

    @Override
    public void close() {
        for (ConfigsTextField textField : this.textFieldsList) {
            if (Pattern.matches(textField.finalValidation, textField.getText())) {
                textField.configsSection.updateConfigs(textField.getText());
            }
        }

        super.close();
    }

    public void scroll(int amount, int mouseX) {
        if (mouseX < this.width / 16 + 100) return;
        int configHeight = settingLineHeight + settingHeight;
        int settingsOnScreen = (this.height - startingHeight) / configHeight;

        // no need to scroll if every setting fits on screen
        if (searchTextField.getText().length() > 0) {
            if (settingsOnScreen > getSectionsBySearch().size()) return;
        } else {
            if (settingsOnScreen > totalSections.get(selectedCategory).size()) return;
        }

        scrollSections += -amount;
        if (scrollSections < 0) scrollSections = 0;
        if (searchTextField.getText().length() > 0) {
            if (scrollSections > getSectionsBySearch().size() - settingsOnScreen)
                scrollSections = getSectionsBySearch().size() - settingsOnScreen;
        } else {
            if (scrollSections > totalSections.get(selectedCategory).size() - settingsOnScreen)
                scrollSections = totalSections.get(selectedCategory).size() - settingsOnScreen;
        }

        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();
        buttonList.addAll(categories);

        ArrayList<ConfigsSection> sectionList;
        if (searchTextField.getText().length() > 0) {
            sectionList = getSectionsBySearch();
        } else {
            sectionList = new ArrayList<>(totalSections.get(selectedCategory).subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        }

        sectionList.forEach((ConfigsSection configsSection) -> {
            int configPlacement = sectionList.indexOf(configsSection);
            if (configsSection.button != null) {
                configsSection.button.y = configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1));
                this.buttonList.add(configsSection.button);
            }

            if (configsSection.textField != null) {
                configsSection.textField.y = configPlacement * settingLineHeight + startingHeight + 2 + (settingHeight * (configPlacement + 1));
                this.textFieldsList.add(configsSection.textField);
            }
        });
    }

    public void addSection(Config config) {
        String configValue = AvoMod2.getConfig(config.configsKey);
        if (!configValue.equals("")) {
            config.defaultValue = configValue;
        }

        ConfigsSection sectionToAdd;

        int configPlacement = 0;
        if (totalSections.get(config.configsCategory) != null) {
            configPlacement = totalSections.get(config.configsCategory).size();
        }
        if (config instanceof ConfigToggle) {
            String[] choices = new String[]{"Enabled", "Disabled"};
            //int x = this.width - (this.width / 4) - (Stream.of(choices).mapToInt((String choice) -> Avomod.getMC().fontRenderer.getStringWidth(choice)).max().getAsInt() + 10);
            int width = Stream.of(choices).mapToInt(textRenderer::getWidth).max().getAsInt() + 10;

            ConfigsButton configButton = new ConfigsButton(this.width / 16 + 121, configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1)), width, choices, config.defaultValue);
            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, configButton, config.configsKey);
        } else {
            ConfigsTextField textField = new ConfigsTextField(((ConfigInput) config).allowedInputs, ((ConfigInput) config).finalValidation, textRenderer, this.width / 16 + 122, configPlacement * settingLineHeight + startingHeight - 2 + (settingHeight * (configPlacement + 1)), 80, 16, this);
            textField.setText(config.defaultValue);
            if (((ConfigInput) config).maxLength != 0) {
                textField.setMaxLength(((ConfigInput) config).maxLength);
            }

            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, textField, config.configsKey);
        }

        // add the ConfigsSection to the configs map
        if (totalSections.containsKey(config.configsCategory)) {
            ArrayList<ConfigsSection> s = totalSections.get(config.configsCategory);
            s.add(sectionToAdd);
            totalSections.put(config.configsCategory, s);
        } else {
            totalSections.put(config.configsCategory, new ArrayList<>(Collections.singletonList(sectionToAdd)));
        }

        // make sure all categories that a config can be in are present in categories
        boolean containsCategory = false;
        for (ConfigsCategory category : categories) {
            if (category.title.equals(config.configsCategory)) {
                containsCategory = true;
                break;
            }
        }
        if (!containsCategory) {
            categories.add(new ConfigsCategory(this.width / 16, startingHeight + categories.size() * settingLineHeight, config.configsCategory, this));
        }
    }
}
