package cf.avicia.avomod2.client.locationselements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public abstract class Element {
    protected float x, y, scale;
    protected Color color;

    public Element(float x, float y, float scale, Color color) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.color = color;
    }

    public Element(String fileData) {
        this(Float.parseFloat(fileData.split(",")[0]),
                Float.parseFloat(fileData.split(",")[1]),
                Float.parseFloat(fileData.split(",")[2]),
                new Color(Integer.parseInt(fileData.split(",")[3])));
    }

    public void move(float changeX, float changeY) {
        setX(getX() + (changeX / scale));
        setY(getY() + (changeY / scale));
    }

    public abstract void draw(MatrixStack matrices);

    public float getTopEdge() {
        return getY() * getScale();
    }

    public float getLeftEdge() {
        return getX() * getScale();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen == null) return null;

        float xProp = (x * scale) / currentScreen.width;
        float yProp = (y * scale) / currentScreen.height;

        return String.format("%s,%s", xProp, yProp);
    }
}
