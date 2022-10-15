package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import cf.avicia.avomod2.client.eventhandlers.chatevents.TriggerChatEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.TriggerHudEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WorldInfoOnTab;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {

    public static Screen screenToRender = null;

    @Override
    public void onInitializeClient() {
        WorldInfoOnTab.updateUpTimes();
        CommandInitializer.initializeCommands();
        ConfigsHandler.initializeConfigs();
        ChatMessageCallback.EVENT.register(TriggerChatEvents::trigger);
        HudRenderCallback.EVENT.register(TriggerHudEvents::trigger);

//        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
//            ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
//                Screen.drawTextWithShadow(matrices, client.textRenderer, Text.of("Hello"), mouseX, mouseY, 0xffffff);
//            });
//        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
        });
    }
}
