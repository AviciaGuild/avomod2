package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.Utils;
import cf.avicia.avomod2.webrequests.wynnapi.GuildStats;
import cf.avicia.avomod2.webrequests.wynnapi.PlayerStats;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

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
                                    context.getSource().sendFeedback(new LiteralText(
                                            "§b" + formattedUsername + "§7 is online on §b" + world));
                                } else if (formattedUsername == null) {
                                    context.getSource().sendFeedback(new LiteralText("§4" + username + "§c is not a Wynncraft player!"));
                                } else {
                                    context.getSource().sendFeedback(new LiteralText("§4" + formattedUsername + "§c is not online!"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }
}
