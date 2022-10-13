package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import cf.avicia.avomod2.client.eventhandlers.chatevents.MakeShoutsClickable;
import cf.avicia.avomod2.client.eventhandlers.chatevents.ShowRealName;
import cf.avicia.avomod2.client.eventhandlers.chatevents.TriggerChatEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommandInitializer.initializeCommands();
        ChatMessageCallback.EVENT.register(TriggerChatEvents::trigger);
    }
}
