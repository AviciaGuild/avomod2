package cf.avicia.avomod2.client.eventhandlers.screenevents;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class TriggerScreenEvents {

    public static void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        ScreenEvents.beforeRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> TriggerScreenEvents.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, matrices, mouseX, mouseY));
    }

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, MatrixStack matrices, int mouseX, int mouseY) {
        TradeMarketIcons.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, matrices, mouseX, mouseY);
        AttackedTerritoryDifficulty.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, matrices, mouseX, mouseY);
    }
}
