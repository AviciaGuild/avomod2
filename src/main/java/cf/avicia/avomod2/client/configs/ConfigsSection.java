package cf.avicia.avomod2.client.configs;

import cf.avicia.avomod2.core.CustomFile;
import net.minecraft.text.Text;

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
        ConfigsHandler.updateConfigs(this.configsKey, newValue);
    }

    public void drawSection(ConfigsGui configsGui, int x, int y) {
        if (button != null) {
            button.setX(x);
            button.setY(y + configsGui.settingHeight - 5);
            configsGui.addButton(button);
        }
        if (textField != null) {
            textField.setX(x + 5);
            textField.setY(y + configsGui.settingHeight);
            configsGui.addTextField(textField);
        }
//        configsGui.drawContext.getMatrices().push();
//        configsGui.drawContext.getMatrices().translate(0f, 0f, 800f);
        configsGui.drawContext.getTextConsumer().text(x, y, Text.of(title));
//        configsGui.drawContext.drawTextWithShadow(MinecraftClient.getInstance().inGameHud.getTextRenderer(), title, x, y, 0xFFFFFF);
//        configsGui.drawContext.getMatrices().pop();
    }
}
