package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import cf.avicia.avomod2.client.customevents.RenderBossBarCallback;
import cf.avicia.avomod2.client.eventhandlers.chatevents.TriggerChatEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.TriggerHudEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WorldInfoOnTab;
import cf.avicia.avomod2.client.eventhandlers.screenevents.TriggerScreenEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {

    public static Screen screenToRender = null;

    @Override
    public void onInitializeClient() {
        WorldInfoOnTab.updateUpTimes();
        CommandInitializer.initializeCommands();
        ConfigsHandler.initializeConfigs();
        ChatMessageCallback.EVENT.register(TriggerChatEvents::onMessage);
        HudRenderCallback.EVENT.register(TriggerHudEvents::onRender);
        RenderBossBarCallback.EVENT.register(TriggerHudEvents::onBossBarRender);

        ScreenEvents.AFTER_INIT.register(TriggerScreenEvents::afterInit);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
        });
    }
}
