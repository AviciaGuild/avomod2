package cf.avicia.avomod2.client.configs.locations;

import cf.avicia.avomod2.client.AvoMod2Client;
import cf.avicia.avomod2.client.configs.ConfigsGui;
import cf.avicia.avomod2.client.eventhandlers.hudevents.AttackTimerMenu;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WorldInfoOnTab;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LocationsGui extends Screen {
    private static boolean isOpen = false;
    private List<ElementGroup> items;


    public LocationsGui() {
        super(Text.of("AvoMod Locations"));

    }

    public static boolean isOpen() {
        return isOpen;
    }

    @Override
    public void init() {
        super.init();
        items = List.of(
//                WarTracker.getElementsToDraw(),
                Objects.requireNonNull(AttackTimerMenu.getElementsToDraw(Arrays.asList("13:47 Otherwordly Monolith", "5:23 Detlas", "9:52 Guild Hall"), true)),
//                TabStatusDisplay.getElementsToDraw(Arrays.asList("Stealth Attack (00:01) x1", "90% Damage Bonus (00:04) x1")),
//                WarDPS.getElementsToDraw(224, 12523563, 24400, 36000),
//                BombBellTracker.getElementsToDraw(BombBellTracker.getSampleData()),
                WorldInfoOnTab.getElementsToDraw()
        );
        Screens.getButtons(this).add(new ResetLocationsButton( this.width / 2 - 50, this.height - 30, 100, 20, "Reset to Defaults", this));
        Screens.getButtons(this).add(new ButtonWidget( 10, 10, 50, 20, Text.of("Configs"), button -> AvoMod2Client.screenToRender = new ConfigsGui()));
        isOpen = true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Makes blur
        this.renderBackground(matrices);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        drawCenteredText(matrices, textRenderer, "AvoMod Locations", this.width / 4 + 1, 11, 0x444444);
        drawCenteredText(matrices, textRenderer, "AvoMod Locations", this.width / 4, 10, 0x1B33CF);
        matrices.pop();

        if (items != null) {
            items.forEach(eg -> eg.drawGuiElement(matrices));
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        items.forEach(e -> e.pickup((int) mouseX, (int) mouseY));

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        items.forEach(e -> e.move((int) mouseX, (int) mouseY));

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        items.forEach(e -> e.release((int) mouseX, (int) mouseY));

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (items != null) {
            items.forEach(ElementGroup::save);
        }
        isOpen = false;
        super.close();
    }
}
