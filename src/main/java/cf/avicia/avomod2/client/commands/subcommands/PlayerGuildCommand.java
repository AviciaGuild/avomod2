package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.wynnapi.GuildStats;
import cf.avicia.avomod2.webrequests.wynnapi.PlayerStats;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class PlayerGuildCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("name", word())
                        .executes(context -> {
                            String username = getString(context, "name");
                            Thread thread = new Thread(() -> {
                                PlayerStats playerStats = new PlayerStats(username);
                                String formattedUsername = playerStats.getUsername();
                                String guild = playerStats.getGuild();
                                String guildRank = playerStats.getGuildRank();
                                if (formattedUsername != null && guild != null && guildRank != null) {
                                    GuildStats guildStats = new GuildStats(guild);
                                    context.getSource().sendFeedback(Text.literal(
                                            "§b" + formattedUsername + "§7 is a §b" + Utils.firstLetterCapital(guildRank) + "§7 in the guild §b" + guild +
                                                    "§7. They have been in the guild for §b" + guildStats.getTimeInGuild(formattedUsername)));
                                } else if (formattedUsername == null) {
                                    context.getSource().sendFeedback(Text.literal("§4" + username + "§c is not a Wynncraft player!"));
                                } else {
                                    context.getSource().sendFeedback(Text.literal("§b" + formattedUsername + "§7 is not in a guild!"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }


}
