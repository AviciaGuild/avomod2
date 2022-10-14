package cf.avicia.avomod2.client.configs;

import cf.avicia.avomod2.core.CustomFile;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

public class ConfigsSection {
    public final String configsCategory;
    private final CustomFile customFile;
    private final String configsKey;
    public ConfigsButton button;
    public ConfigsTextField textField;
    public String title;

    public ConfigsSection(String configsCategory, String title, ConfigsButton button, String configsKey) {
        this.title = title;
        this.button = button;
        this.configsKey = configsKey;
        this.configsCategory = configsCategory;

        this.button.setConfigsSection(this);
        this.customFile = new CustomFile(ConfigsHandler.getConfigPath("configs"));
    }

    public ConfigsSection(String configsCategory, String title, ConfigsTextField textField, String configsKey) {
        this.title = title;
        this.textField = textField;
        this.configsKey = configsKey;
        this.configsCategory = configsCategory;

        this.textField.setConfigsSection(this);
        this.customFile = new CustomFile(ConfigsHandler.getConfigPath("configs"));
    }

    public void updateConfigs(String newValue) {
        JsonObject configsJson = this.customFile.readJson();
        configsJson.addProperty(this.configsKey, newValue);

        if (this.configsKey.equals("autoStream") && newValue.equals("Disabled")) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendChatMessage("/stream");
            }
        }

        ConfigsHandler.configs = configsJson;
        this.customFile.writeJson(configsJson);
    }

    public void drawSection(ConfigsGui configsGui, int x, int y) {
        MinecraftClient.getInstance().inGameHud.getTextRenderer().drawWithShadow(configsGui.matrices, title, (float) x, (float) y, 0xFFFFFF);

        if (button != null) {
            button.x = x;
            button.y = y + configsGui.settingHeight - 5;
            configsGui.addButton(button);
        }
        if (textField != null) {
            textField.x = x + 5;
            textField.y = y + configsGui.settingHeight;
            configsGui.addTextField(textField);
        }
    }
}
