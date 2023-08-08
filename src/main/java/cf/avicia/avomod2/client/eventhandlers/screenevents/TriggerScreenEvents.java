package cf.avicia.avomod2.client.eventhandlers.screenevents;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class TriggerScreenEvents {

    public static void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        ScreenEvents.beforeRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) -> TriggerScreenEvents.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY));
        ScreenEvents.afterRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) -> TriggerScreenEvents.afterRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY));
    }

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        TradeMarketIcons.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY);
        AttackedTerritoryDifficulty.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY);
    }
    public static void afterRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        GuildBankKeybind.afterRender(client, screen, screen1);
    }
}
