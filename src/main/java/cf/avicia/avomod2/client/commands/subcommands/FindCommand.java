package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.webrequests.wynnapi.PlayerStats;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class FindCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("name", word())
                        .executes(context -> {
                            String username = getString(context, "name");
                            Thread thread = new Thread(() -> {
                                PlayerStats playerStats = new PlayerStats(username);
                                String formattedUsername = playerStats.getUsername();
                                String world = playerStats.getServer();
                                if (formattedUsername != null && world != null) {
                                    context.getSource().sendFeedback(Text.literal(
                                            "§b" + formattedUsername + "§7 is online on §b" + world));
                                } else if (formattedUsername == null) {
                                    context.getSource().sendFeedback(Text.literal("§4" + username + "§c is not a Wynncraft player!"));
                                } else {
                                    context.getSource().sendFeedback(Text.literal("§4" + formattedUsername + "§c is not online!"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }
}
