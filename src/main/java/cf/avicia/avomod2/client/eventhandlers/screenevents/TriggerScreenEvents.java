package cf.avicia.avomod2.client.eventhandlers.screenevents;

import cf.avicia.avomod2.client.eventhandlers.hudevents.ProfessionHighlighter;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public class TriggerScreenEvents {

    public static void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof GenericContainerScreen) {
            ProfessionHighlighter.addProfessionButton(screen, scaledWidth, scaledHeight);
        }
        ScreenEvents.beforeRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) -> TriggerScreenEvents.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY));
        ScreenEvents.afterRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) -> TriggerScreenEvents.afterRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY));
    }

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        TradeMarketIcons.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY);
        AttackedTerritoryDifficulty.beforeRender(client, screen, scaledWidth, scaledHeight, screen1, drawContext, mouseX, mouseY);
    }
    public static void afterRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        GuildBankKeybind.afterRender(client, screen, screen1);
        ProfessionHighlighter.render(screen1, client, drawContext, scaledWidth, scaledHeight);
    }
}
