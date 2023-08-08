package cf.avicia.avomod2.client.configs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class ConfigsCategory extends ButtonWidget {
    public String title;
    public boolean enabled;
    public boolean hasSearchItem;
    private final int x;
    private final int y;
    private final ConfigsGui configsGui;

    public ConfigsCategory(int x, int y, String title, ConfigsGui configsGui) {
        super(x, y, 100, 20, Text.of(title), ButtonWidget::onPress, DEFAULT_NARRATION_SUPPLIER);
        this.title = title;
        this.x = x;
        this.y = y;
        this.enabled = false;

        this.configsGui = configsGui;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        configsGui.setCategory(title);
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        int color = 0xBBBBBB;

        if (hasSearchItem) {
            int width = MinecraftClient.getInstance().textRenderer.getWidth(title);
            drawContext.drawHorizontalLine(x + 48 - width / 2, x + 52 + width / 2, y + 16, Color.WHITE.getRGB());

            color = 0xFFFFFF;
        }

        if (enabled) {
            drawContext.fill(x, y, x + 100, y + 1, 0xFFFFFFFF);
            drawContext.fill(x + 99, y, x + 100, y + 20, 0xFFFFFFFF);
            drawContext.fill(x, y, x + 1, y + 20, 0xFFFFFFFF);
            drawContext.fill(x, y + 19, x + 100, y + 20, 0xFFFFFFFF);

            color = 0xFFFFFF;
        }

        drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, title, x + 50, y + 6, color);
    }
}