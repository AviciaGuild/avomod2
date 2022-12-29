package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.AvoMod2Client;
import cf.avicia.avomod2.client.configs.ConfigsGui;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;


public class ConfigsCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .executes(context -> {
                    AvoMod2Client.screenToRender = new ConfigsGui();
                    return 0;
                });
    }
}
