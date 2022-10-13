package cf.avicia.avomod2.client.configs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;

public class ConfigsButton extends ButtonWidget {
    public String[] choices;
    private ConfigsSection configsSection;
    private int currentIndex;

    public ConfigsButton(int x, int y, int width, String[] choices, String defaultValue) {
        super(x, y, width, 20, Text.of(defaultValue), ButtonWidget::onPress);
        this.currentIndex = Arrays.asList(choices).indexOf(defaultValue);
        this.choices = choices;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.currentIndex++;
        if (this.currentIndex == choices.length) {
            this.currentIndex = 0;
        }
        this.setMessage(Text.of(this.choices[this.currentIndex]));

        if (this.configsSection != null) {
            this.configsSection.updateConfigs(this.getMessage().getString());
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(matrices, x + width - 1, y, x + width, y + 20, 0xFFFFFFFF);
        fill(matrices, x, y, x + 1, y + 20, 0xFFFFFFFF);
        fill(matrices, x, y + 19, x + width, y + 20, 0xFFFFFFFF);
        int color = 0xFF8888;
        if (choices[currentIndex].equals("Enabled")) color = 0x88FF88;
        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, choices[currentIndex], x + width / 2, y + 6, color);
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
