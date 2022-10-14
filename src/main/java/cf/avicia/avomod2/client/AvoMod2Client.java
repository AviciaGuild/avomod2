package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import cf.avicia.avomod2.client.eventhandlers.chatevents.TriggerChatEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {

    public static  Screen screenToRender = null;
    @Override
    public void onInitializeClient() {
        CommandInitializer.initializeCommands();
        ChatMessageCallback.EVENT.register(TriggerChatEvents::trigger);
        ConfigsHandler.initializeConfigs();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
        });
    }
}
