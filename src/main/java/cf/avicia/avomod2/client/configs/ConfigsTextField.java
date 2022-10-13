package cf.avicia.avomod2.client.configs;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class ConfigsTextField extends TextFieldWidget {
    public ConfigsSection configsSection;
    public String allowedInputs, finalValidation;
//    public Color borderColor;
    private ConfigsGui cfgui;

    public ConfigsTextField(String allowedInputs, String finalValidation, TextRenderer textRenderer, int x, int y, int par5Width, int par6Height, ConfigsGui cfgui) {
        super(textRenderer, x + 4, y + 4, par5Width, par6Height, Text.of(""));
        this.allowedInputs = allowedInputs;
        this.finalValidation = finalValidation;
        this.cfgui = cfgui;

        this.setDrawsBackground(false);
    }

//    @Override
//    public void drawTextBox() {
//        if (Pattern.matches(this.finalValidation, this.getText())) {
//            borderColor = new Color(0, 255, 0, 200);
//        } else {
//            borderColor = new Color(255, 0, 0, 200);
//        }
//
//        int modifiedX = this.x - 4;
//        int modifiedY = this.y - 4;
//        drawRect(modifiedX - 1, modifiedY - 1, modifiedX + this.width + 1, modifiedY + this.height + 1, borderColor.getRGB());
//        drawRect(modifiedX, modifiedY, modifiedX + this.width, modifiedY + this.height, -16777216);
//
//        super.drawTextBox();
//    }

//    @Override
//    public void setFocused(boolean isFocusedIn) {
//        boolean oldFocused = this.isFocused();
//        super.setFocused(isFocusedIn);
//
//        if (oldFocused == isFocusedIn) return;
//
//        cfgui.textFieldIsFocused = cfgui.textFieldsList.stream().anyMatch(GuiTextField::isFocused);
//        if (isFocusedIn) {
////            cfgui.searchTextField.setFocused(false);
//            cfgui.textFieldIsFocused = true;
//        } else if (!cfgui.textFieldIsFocused) {
////            cfgui.searchTextField.setFocused(true);
//        }
//    }

    @Override
    public void write(String text) {
        if (text.length() == 0 || Pattern.matches(this.allowedInputs, text)) {
            super.write(text);
        }
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
