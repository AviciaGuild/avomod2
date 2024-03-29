package cf.avicia.avomod2.client.locationselements;

import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class RectangleElement extends Element {
    private float width, height;

    public RectangleElement(float x, float y, float width, float height, float scale, Color color) {
        super(x, y, scale, color);
        this.width = width;
        this.height = height;
    }

    public RectangleElement(float x, float y, float width, float height, Color color) {
        this(x, y, width, height, 1F, color);
    }

    public void draw(DrawContext drawContext) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scale, scale, scale);
        drawContext.fill((int) x, (int) y, (int) (x + width), (int) (y + height), color.getRGB());
        drawContext.getMatrices().pop();
    }

    public float getRightEdge() {
        return (getX() + getWidth()) * getScale();
    }

    public float getBottomEdge() {
        return (getY() + getHeight()) * getScale();
    }

    public boolean inRectangle(int mouseX, int mouseY) {
        return mouseX >= getLeftEdge() && mouseX <= getRightEdge() &&
                mouseY >= getTopEdge() && mouseY <= getBottomEdge();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
