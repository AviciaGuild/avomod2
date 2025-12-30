package cf.avicia.avomod2.client.locationselements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;

public class TextElement extends Element {
    protected String text;

    public TextElement(String text, float x, float y, float scale, Color color) {
        super(x, y, scale, color);
        this.text = text;
    }

    public TextElement(String text, float x, float y, Color color) {
        this(text, x, y, 1F, color);
    }

    public void draw(DrawContext drawContext) {
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(scale, scale, drawContext.getMatrices());
        drawContext.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of(text), (int) x, (int) y, color.getRGB());
        drawContext.getMatrices().popMatrix();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
