package cf.avicia.avomod2.client.configs;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchTextField extends TextFieldWidget {
    private final ConfigsGui cfgui;

    private MatrixStack matrices;

    public SearchTextField(MatrixStack matrices, TextRenderer textRenderer, int x, int y, int par5Width, int par6Height, ConfigsGui cfgui) {
        super(textRenderer, x, y, par5Width, par6Height, Text.of(""));
        this.matrices = matrices;
        this.cfgui = cfgui;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        String oldText = this.getText();

        boolean output = super.keyPressed(keyCode, scanCode, modifiers);
        if (!this.isFocused()) return output;

        if (this.getText().length() > 0) {
            if (oldText.length() == 0) {
                cfgui.categories.add(new ConfigsCategory(cfgui.width / 16,
                        cfgui.startingHeight + cfgui.categories.size() * cfgui.settingLineHeight,
                        "All", cfgui));
                cfgui.categories.get(cfgui.categories.size() - 1).hasSearchItem = true;
                cfgui.setCategory("All");
            }

            ArrayList<ConfigsSection> selectionSections = cfgui.getSectionsBySearch("All");
            if (selectionSections.size() == 0) {
                cfgui.categories.forEach(e -> e.hasSearchItem = false);
            } else {
                cfgui.categories.get(cfgui.categories.size() - 1).hasSearchItem = true;

                selectionSections.forEach(selectionSection -> {
                    cfgui.categories.stream().filter(e -> e.title.equals(selectionSection.configsCategory)).findFirst().get().hasSearchItem = true;
                });
            }

            cfgui.drawSections(matrices, selectionSections);
        } else if (oldText.length() != this.getText().length()) {
            cfgui.categories = cfgui.categories.stream().filter(e -> !e.title.equals("All")).collect(Collectors.toList());
            cfgui.setCategory(cfgui.savedCategory);

            cfgui.categories.forEach(e -> e.hasSearchItem = false);
        }

        return output;
    }
}
