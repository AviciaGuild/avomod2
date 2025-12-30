package cf.avicia.avomod2.client.configs.locations;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.core.CustomFile;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.AbstractInput;


import java.util.Map;

public class ResetLocationsButton extends ButtonWidget {

    private final LocationsGui gui;

    public ResetLocationsButton(int x, int y, int widthIn, int heightIn, String buttonText, LocationsGui gui) {
        super(x, y, widthIn, heightIn, net.minecraft.text.Text.of(buttonText), (widget) -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.gui = gui;
    }

    @Override
    public void onPress(AbstractInput input) {
        CustomFile locationsFile = new CustomFile(ConfigsHandler.getConfigPath("locations"));
        JsonObject locationsJson = locationsFile.readJson();

        for (Map.Entry<String, String> locationData : LocationsHandler.defaultLocations.entrySet()) {
            locationsJson.addProperty(locationData.getKey(), locationData.getValue());
        }

        locationsFile.writeJson(locationsJson);
        LocationsHandler.locations = locationsJson;
        gui.init();
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(getX(), getY(), getX() + width, getY() + 1, 0xFFFFFFFF);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + 20, 0xFFFFFFFF);
        context.fill(getX(), getY(), getX() + 1, getY() + 20, 0xFFFFFFFF);
        context.fill(getX(), getY() + 19, getX() + width, getY() + 20, 0xFFFFFFFF);

        this.drawTextWithMargin(context.getTextConsumer(), this.getMessage(), 0);
    }
}
