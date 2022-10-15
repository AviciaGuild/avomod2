package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class AutoStreamCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .executes(context -> {
                    boolean autoStream = ConfigsHandler.getConfigBoolean("autoStream");
                    ConfigsHandler.updateConfigs("autoStream", (!autoStream) ? "Enabled" : "Disabled");
                    return 0;
                });
    }
}
