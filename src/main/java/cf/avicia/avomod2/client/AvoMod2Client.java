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
import cf.avicia.avomod2.utils.BeaconManager;
import cf.avicia.avomod2.utils.TerritoryData;
import cf.avicia.avomod2.webrequests.TerritoryDataHttpServer;
import com.sun.net.httpserver.HttpServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

import java.io.IOException;
import java.net.InetSocketAddress;

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

        ChatMessageCallback.EVENT.register(TriggerChatEvents::onMessage);
        HudRenderCallback.EVENT.register(TriggerHudEvents::onRender);
        RenderBossBarCallback.EVENT.register(TriggerHudEvents::onBossBarRender);
        ChatMouseClickedCallback.EVENT.register(TriggerChatMouseClickedEvents::mouseClicked);
        InventoryMouseClickedCallback.EVENT.register(TriggerInventoryMouseClickedEvents::mouseClicked);

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            BeaconManager.onWorldRender(context);
            WarTracker.afterEntityRender();
        });

        ScreenEvents.AFTER_INIT.register(TriggerScreenEvents::afterInit);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TerritoryData.onTick();
            GuildBankKeybind.onTick();
            WarTracker.onTick();

            if (screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
        });

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(50036), 0);
            server.createContext("/api/data", new TerritoryDataHttpServer());
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Territory data started on port 50036");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
