package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.customevents.ChatMessageCallback;
import cf.avicia.avomod2.client.customevents.ChatMouseClickedCallback;
import cf.avicia.avomod2.client.customevents.InventoryMouseClickedCallback;
import cf.avicia.avomod2.client.customevents.RenderBossBarCallback;
import cf.avicia.avomod2.client.eventhandlers.chatclickedevents.TriggerChatMouseClickedEvents;
import cf.avicia.avomod2.client.eventhandlers.chatevents.TriggerChatEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.TriggerHudEvents;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WarTracker;
import cf.avicia.avomod2.client.eventhandlers.hudevents.WorldInfoOnTab;
import cf.avicia.avomod2.client.eventhandlers.inventoryclickedevents.TriggerInventoryMouseClickedEvents;
import cf.avicia.avomod2.client.eventhandlers.screenevents.GuildBankKeybind;
import cf.avicia.avomod2.client.eventhandlers.screenevents.TriggerScreenEvents;
import cf.avicia.avomod2.client.renderer.TerritoryOutlineRenderer;
import cf.avicia.avomod2.utils.BeaconManager;
import cf.avicia.avomod2.utils.TerritoryData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;


@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {

    public static Screen screenToRender = null;

    @Override
    public void onInitializeClient() {
        WorldInfoOnTab.updateUpTimes();
        ConfigsHandler.initializeConfigs();
        LocationsHandler.initializeLocations();
        CommandInitializer.initializeCommands();
        GuildBankKeybind.init();
        TerritoryOutlineRenderer.initKeybind();

        ChatMessageCallback.EVENT.register(TriggerChatEvents::onMessage);
        HudRenderCallback.EVENT.register((context, renderTickCounter) -> TriggerHudEvents.onRender(context));
        RenderBossBarCallback.EVENT.register(TriggerHudEvents::onBossBarRender);
        ChatMouseClickedCallback.EVENT.register(TriggerChatMouseClickedEvents::mouseClicked);
        InventoryMouseClickedCallback.EVENT.register(TriggerInventoryMouseClickedEvents::mouseClicked);

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            BeaconManager.onWorldRender(context);
            WarTracker.afterEntityRender();
            TerritoryOutlineRenderer.renderOutline(context);
        });

        ScreenEvents.AFTER_INIT.register(TriggerScreenEvents::afterInit);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TerritoryData.onTick();
            GuildBankKeybind.onTick();
            TerritoryOutlineRenderer.onTick();
            WarTracker.onTick();

            if (screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
        });

    }
}
