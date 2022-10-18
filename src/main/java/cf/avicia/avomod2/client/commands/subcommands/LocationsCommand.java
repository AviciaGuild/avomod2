package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.AvoMod2Client;
import cf.avicia.avomod2.client.configs.ConfigsGui;
import cf.avicia.avomod2.client.configs.locations.LocationsGui;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;


public class LocationsCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .executes(context -> {
                    AvoMod2Client.screenToRender = new LocationsGui();
                    return 0;
                });
    }
}
