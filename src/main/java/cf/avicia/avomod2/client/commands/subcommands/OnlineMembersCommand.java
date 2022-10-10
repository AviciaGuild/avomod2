package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.webrequests.wynnapi.GuildStats;
import cf.avicia.avomod2.webrequests.wynnapi.PlayerList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class OnlineMembersCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("guildName", greedyString())
                        .executes(context -> {
                            Thread thread = new Thread(() -> {
                                try {
                                    String guildName = getString(context, "guildName");
                                    GuildStats guildStats = new GuildStats(guildName);
                                    PlayerList playerList = new PlayerList();
                                    JsonArray guildMembers = guildStats.getMembers();
                                    List<String> membersWithRankFormatting = new ArrayList<>();
                                    if (guildMembers != null) {
                                        for (JsonElement guildMember : guildMembers) {
                                            JsonObject memberData = guildMember.getAsJsonObject();
                                            if (playerList.isPlayerOnline(memberData.get("name").getAsString())) {
                                                membersWithRankFormatting.add(guildStats.getWithRankFormatting(memberData.get("name").getAsString()));
                                            }
                                        }
                                        membersWithRankFormatting.sort(String::compareToIgnoreCase);
                                        context.getSource().sendFeedback(new LiteralText("§b" + guildStats.getName() + "§3 [§b" + guildStats.getPrefix() + "§3]§7 has §b"
                                                + membersWithRankFormatting.size() + "§7 of §b" + guildMembers.size() + "§7 members online: §b" + String.join(", ", membersWithRankFormatting)
                                                .replaceAll("\\*", "\u2605") // Make the guild stars look good
                                        ));
                                    } else {
                                        context.getSource().sendFeedback(new LiteralText("§cGuild §4"+ guildName + "§c not found"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    context.getSource().sendFeedback(new LiteralText("§cGuild not found"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }


}
