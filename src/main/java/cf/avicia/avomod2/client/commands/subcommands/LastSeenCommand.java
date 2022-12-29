package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.webrequests.wynnapi.PlayerStats;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class LastSeenCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("name", word())
                        .executes(context -> {
                            String username = getString(context, "name");
                            Thread thread = new Thread(() -> {
                                PlayerStats playerStats = new PlayerStats(username);
                                String formattedUsername = playerStats.getUsername();
                                String world = playerStats.getServer();
                                String timeSinceLastJoin = playerStats.getTimeSinceLastJoin();
                                if (formattedUsername != null && world != null) {
                                    context.getSource().sendFeedback(Text.literal("§b" + formattedUsername + "§7 is online on §b" + world));
                                } else if(timeSinceLastJoin != null) {
                                    context.getSource().sendFeedback(Text.literal("§b" + formattedUsername + "§7 was last seen §b" + timeSinceLastJoin + "§7 ago."));
                                } else {
                                    context.getSource().sendFeedback(Text.literal("§4" + username + "§c is not a Wynncraft player!"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }
}
