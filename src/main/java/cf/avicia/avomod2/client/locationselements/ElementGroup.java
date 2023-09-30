package cf.avicia.avomod2.client.locationselements;

import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.List;

public class ElementGroup {
    private final List<Element> elementsList;
    private final String key;
    private final float scale;
    private int startX, startY;
    private boolean clicked = false;
    private boolean leftAlign = true;

    public ElementGroup(String key, float scale, List<Element> elementsList) {
        this.key = key;
        this.elementsList = elementsList;
        this.scale = scale;

        updateAlignment();
    }

    public void draw(DrawContext drawContext) {
        if (!LocationsGui.isOpen()) {
            elementsList.forEach(e -> e.draw(drawContext));
        }
    }

    public void drawGuiElement(DrawContext drawContext) {
        elementsList.forEach(e -> e.draw(drawContext));
    }

    public void pickup(int mouseX, int mouseY) {
        elementsList.forEach(element -> {
            if (element instanceof RectangleElement && ((RectangleElement) element).inRectangle(mouseX, mouseY)) {
                startX = mouseX;
                startY = mouseY;
                clicked = true;
            }
        });
    }

    public void move(int mouseX, int mouseY) {
        if (!clicked) return;

        elementsList.forEach(element -> element.move(mouseX - startX, mouseY - startY));
        startX = mouseX;
        startY = mouseY;

        updateAlignment();
    }


    private HashMap<String, Float> getMaxAndMinEdges() {
        HashMap<String, Float> result = new HashMap<>();
        List<RectangleElement> rectangleElementList = elementsList.stream().filter(RectangleElement.class::isInstance).map(RectangleElement.class::cast).toList();
        result.put("minLeftEdge", rectangleElementList.stream().map(Element::getLeftEdge).min(Float::compare).orElse((float) 0) / scale);
        result.put("maxRightEdge", rectangleElementList.stream().map(RectangleElement::getRightEdge).max(Float::compare).orElse((float) 0) / scale);
        result.put("minTopEdge",  rectangleElementList.stream().map(Element::getTopEdge).min(Float::compare).orElse((float) 0) / scale);
        result.put("maxBottomEdge",  rectangleElementList.stream().map(RectangleElement::getBottomEdge).max(Float::compare).orElse((float) 0) / scale);
        return result;
    }
    public void updateAlignment() {
        HashMap<String, Float> elementEdges = getMaxAndMinEdges();
        float minLeftEdge = elementEdges.get("minLeftEdge");
        float maxRightEdge = elementEdges.get("maxRightEdge");
        float screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth() / scale;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (leftAlign && minLeftEdge + (maxRightEdge - minLeftEdge) / 2 > screenWidth / 2) {
            leftAlign = false;

            elementsList.forEach(element -> {
                if (element instanceof RectangleElement) {
                    element.setX(maxRightEdge - ((RectangleElement) element).getWidth());
                } else if (element instanceof TextElement) {
                    element.setX(maxRightEdge - textRenderer.getWidth(((TextElement) element).getText()) - 2);
                }
            });
        } else if (!leftAlign && minLeftEdge + (maxRightEdge - minLeftEdge) / 2 < screenWidth / 2) {
            leftAlign = true;

            elementsList.forEach(element -> {
                if (element instanceof RectangleElement) {
                    element.setX(minLeftEdge);
                } else if (element instanceof TextElement) {
                    element.setX(minLeftEdge + 2);
                }
            });
        }
    }

    public void release(int mouseX, int mouseY) {
        if (!clicked) return;

        startX = 0;
        startY = 0;
        clicked = false;
    }

    public void save() {
        LocationsHandler.save(this);
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        HashMap<String, Float> elementEdges = getMaxAndMinEdges();
        float minLeftEdge = elementEdges.get("minLeftEdge");
        float maxRightEdge = elementEdges.get("maxRightEdge");
        float minTopEdge = elementEdges.get("minTopEdge");

        float screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth() / scale;
        float screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight() / scale;
        float xProp = ((int) minLeftEdge) / screenWidth;
        float yProp = ((int) minTopEdge) / screenHeight;

        if (!leftAlign) {
            xProp = ((int) maxRightEdge) / screenWidth;
        }

        return xProp + "," + yProp + "," + leftAlign;
    }
}
