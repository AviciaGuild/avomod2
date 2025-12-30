package cf.avicia.avomod2.client.configs;

import cf.avicia.avomod2.client.AvoMod2Client;
import cf.avicia.avomod2.client.configs.locations.LocationsGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.util.Arrays;

public class ConfigsButton extends ButtonWidget {
    public String[] choices;
    private ConfigsSection configsSection;
    private int currentIndex;

    public ConfigsButton(int x, int y, int width, String[] choices, String defaultValue) {
        super(x, y, width, 20, net.minecraft.text.Text.of(defaultValue), (input) -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.currentIndex = Arrays.asList(choices).indexOf(defaultValue);
        this.choices = choices;
    }

    @Override
    public void onPress(AbstractInput input) {
        if (this.getMessage().getString().equals("Edit")) {
            AvoMod2Client.screenToRender = new LocationsGui();
            return;
        }
        this.currentIndex++;
        if (this.currentIndex == choices.length) {
            this.currentIndex = 0;
        }
        this.setMessage(net.minecraft.text.Text.of(this.choices[this.currentIndex]));

        if (this.configsSection != null) {
            this.configsSection.updateConfigs(this.getMessage().getString());
        }
    }

    @Override
    protected void drawIcon(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        drawContext.fill(getX(), getY(), getX() + width, getY() + 1, 0xFFFFFFFF);
        drawContext.fill(getX() + width - 1, getY(), getX() + width, getY() + 20, 0xFFFFFFFF);
        drawContext.fill(getX(), getY(), getX() + 1, getY() + 20, 0xFFFFFFFF);
        drawContext.fill(getX(), getY() + 19, getX() + width, getY() + 20, 0xFFFFFFFF);
        int color = 0xFF8888;
        if (choices[currentIndex].equals("Enabled")) color = 0x88FF88;
        if (choices[currentIndex].equals("Edit")) color = 0xFFFF00;

        MutableText text = net.minecraft.text.Text.literal(choices[currentIndex]);
        text.setStyle(Style.EMPTY.withColor(color));
        this.drawTextWithMargin(drawContext.getTextConsumer(), text, 0);
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
